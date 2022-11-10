package com.knocksfornometer.mapimage;

import static com.knocksfornometer.mapimage.xml.XmlUtils.loadAsXmlDocument;
import static com.knocksfornometer.mapimage.xml.XmlUtils.writeXmlDocumentToFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.knocksfornometer.mapimage.data.ElectionData;
import com.knocksfornometer.mapimage.data.ElectionDataManager;
import com.knocksfornometer.mapimage.data.ElectionYear;
import com.knocksfornometer.mapimage.data.ElectionYearDataSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.knocksfornometer.mapimage.imagegeneration.ConstituencyVoteDistributionImageGeneratorExact;
import com.knocksfornometer.mapimage.imagegeneration.ImageGenerator;

/**
 * Generate a map of the UK showing the voting distribution per constituency.
 *
 * As seen on https://en.wikipedia.org/wiki/United_Kingdom_general_election,_2015#Voting_distribution_per_constituency
 *
 * <p>
 * Program does the following; for each election year:
 * <ul>
 *   <li>reads in a map of the UK in SVG format - https://commons.wikimedia.org/wiki/File:2015UKElectionMap.svg</li>
 *   <li>reads in election data</li>
 *   <li>looks up party colors (no vote = BLACK) and calculates a voting distribution for each constituency</li>
 *   <li>generates a 'voting distribution' image for each constituency using an {@link ImageGenerator}
 *   	(pixels represent the party colour and appear in frequency proportional to the votes).</li>
 *   <li>populates the constituencies in the image map with the generated images</li>
 *   <li>writes the updated SVG file</li>
 * </ul>
 *
 * @author Eelco de Vlieger
 */
public class Main {

	private static final int NUM_IMAGE_GENERATION_THREADS = Runtime.getRuntime().availableProcessors();

	private static final File RESOURCES_DIRECTORY = new File("src\\main\\resources");
	private static final String TARGET_OUTPUT_IMAGE_FORMAT = "png";
	private static final String TARGET_OUTPUT_BASE_DIR = "target\\map\\";
	private static final String TARGET_OUTPUT_IMAGE_DIR = "\\constituencies\\";
	private static final String SVG_MAP_OUTPUT_FILE = "UKElectionMap_votes.svg";
	private static final int IMAGE_WIDTH = 100;
	private static final int IMAGE_HEIGHT = 100;
	
	/**
	 * If true  - the image data gets embedded directly in the SVG file (base64 encoded).
	 * If false - the images get linked as external files.
	 */
	private static final boolean EMBED_IMAGE_IN_SVG = true;

	
	public static void main(final String... args) throws Exception {
		for(ElectionYearDataSource electionYearDataSource : getElectionYearDataSources(args)) {
			System.out.println("Loading election data [electionYearDataSource=" + electionYearDataSource + "]");
			ElectionData electionData = ElectionDataManager.getElectionData(electionYearDataSource);
			processElectionData(electionData, electionYearDataSource);
		}
		
		System.out.println("Completed");
	}

	/**
	 * Default to all ElectionYearDataSource values when no specific ones are requested.
	 */
	private static ElectionYearDataSource[] getElectionYearDataSources(final String... electionYears) {
		if(electionYears == null || electionYears.length == 0){
			return ElectionYearDataSource.values();
		}

		return Arrays.stream(electionYears)
				.map(Integer::valueOf)
				.map(ElectionYear::get)
				.map(ElectionYearDataSource::getElectionYearDataSourceByYear)
				.flatMap(Collection::stream)
				.toArray(ElectionYearDataSource[]::new);
	}

	private static void processElectionData(final ElectionData electionData, final ElectionYearDataSource electionYearDataSource) throws Exception {
		System.out.println("generate a 'voting distribution' image for each constituency [electionYearDataSource=" + electionYearDataSource + "]");
		final Map<String, BufferedImage> images = generateImages(electionData.getElectionDataMap(), electionData);

		System.out.println("update SVG map image and save the resulting file [electionYearDataSource=" + electionYearDataSource + "]");
		processSvg(images, electionData);
	}

	/**
	 * Read SVG -> update constituency images -> write SVG
	 */
	private static void processSvg(final Map<String, BufferedImage> images, final ElectionData electionData) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, TransformerException {
		final Map<String, String> constituencyKeyNameToImageMap = images.keySet().stream().collect( Collectors.toMap(electionData.getConstituencyKeyGenerator()::toKey, Function.identity()) );

		final Document doc = loadAsXmlDocument( new File(RESOURCES_DIRECTORY, electionData.getSvgMapInputFile()) );

		final XPath xpath = XPathFactory.newInstance().newXPath();

		final Set<String> matches = new HashSet<>();
		final Set<String> noMatch = new TreeSet<>();

		// specific 'hack' to remove a rogue path from the input SVG
		final Node pathToRemove = ((NodeList) xpath.compile("//*[@id='path717']").evaluate(doc, XPathConstants.NODESET)).item(0);
		if(pathToRemove != null)
			pathToRemove.getParentNode().removeChild(pathToRemove);
		
		System.out.println("SVG :: link constituency paths to images");
		linkConstituencyPathsToImages(constituencyKeyNameToImageMap, doc, xpath, matches, noMatch, electionData.getConstituencyKeyGenerator());

		addXlinkNamespace(doc);

		System.out.println("SVG :: generate pattern image definitions");
		generatePatternImageDefs(doc, xpath, matches, images, electionData);
		
		System.out.println("constituency names matching results [match=" + matches.size() + ", noMatch=" + noMatch.size() + "]");
		if(noMatch.size() > 0)
			System.out.println("noMatch=" + noMatch);

		System.out.println("SVG :: save updated file");
		final ElectionYearDataSource source = electionData.getElectionYearDataSource();
		writeXmlDocumentToFile(doc, TARGET_OUTPUT_BASE_DIR + source + "\\" + source.getElectionYear().getYear() + SVG_MAP_OUTPUT_FILE);
	}

	/**
	 * Links constituency paths to the generated images 
	 */
	private static void linkConstituencyPathsToImages(final Map<String, String> constituencyKeyToImageName,
													  final Document doc,
													  final XPath xpath,
													  final Set<String> matches,
													  final Set<String> noMatch,
													  final ConstituencyKeyGenerator constituencyKeyGenerator) throws XPathExpressionException {

		// Iterate over all constituency SVG paths using XPath to select the DOM nodes
		final NodeList pathNodes = ((NodeList) xpath.compile("//path").evaluate(doc, XPathConstants.NODESET));
		for (int i = 0; i < pathNodes.getLength(); i++) {
			final Node pathNode = pathNodes.item(i);
			final NamedNodeMap attributes = pathNode.getAttributes();
			final Node constituencyAttribute = attributes.getNamedItem("id");
			final String constituency = constituencyAttribute.getNodeValue();

			if(constituency.startsWith("path")) {
				continue; // only care about names constituencies
			}
			
			final String constituencyKey = constituencyKeyGenerator.toKey(constituency);
			final String sourceConstituency = constituencyKeyToImageName.get( constituencyKey );
			
			// Update the style attribute to link to the images by ID
			String style = "fill:url(#" + constituencyKey + ")";
			Node styleAttribute = attributes.getNamedItem("style");
			if(styleAttribute == null){
				// if no style exists, add it
				styleAttribute = pathNode.getOwnerDocument().createAttribute("style");
			    attributes.setNamedItem(styleAttribute);
			}
			
			if( sourceConstituency != null ){
				matches.add(sourceConstituency);

				String originalStyle = styleAttribute.getNodeValue();
				int semicolonIndex = originalStyle.indexOf(";");
				if(semicolonIndex >= 0)
					style += originalStyle.substring(semicolonIndex);
				style = style.replaceAll("&", "&amp;");
				// increase the constituency border line width
				style = style.replace("stroke-width:0.5", "stroke-width:0.8");
			}else{
				noMatch.add(constituencyKey);
				if(styleAttribute != null){
					String originalStyle = styleAttribute.getNodeValue();
					style = originalStyle.replaceFirst("fill:#......", "fill:#AAAAAA");
				}
			}
			styleAttribute.setNodeValue(style);
		}
	}

	/**
	 * Generates the Image Pattern definitions in the SVG file.
	 * 
	 * {@link #EMBED_IMAGE_IN_SVG} controls whether the image data gets embedded directly in the svg file
	 * or gets linked to external image files in {@value #TARGET_OUTPUT_IMAGE_DIR}
	 */
	private static void generatePatternImageDefs(final Document doc,
												 final XPath xpath,
												 final Set<String> matches,
												 final Map<String, BufferedImage> images,
												 final ElectionData electionData) throws XPathExpressionException, IOException {
		final NodeList nodes = (NodeList) xpath.compile("//defs").evaluate(doc, XPathConstants.NODESET);
		Node defsNode = nodes.item(0);
		if(defsNode == null){
			// insert <defs> element
			Element root = doc.getDocumentElement();
			defsNode = doc.createElement("defs");
			root.appendChild(defsNode);
		}
		
		for (String constituency : matches) {
			// <pattern id="Aberavon" patternUnits="userSpaceOnUse" height="100" width="100">
			//    <image x="0" y="0" height="100" width="100" xlink:href=".\constituencies\Aberavon.png"></image>
			// </pattern>
			final Element patternNode = doc.createElement("pattern");
			final String constituencyNameKey = electionData.getConstituencyKeyGenerator().toKey(constituency);
			patternNode.setAttribute("id", constituencyNameKey);
			patternNode.setAttribute("patternUnits", "userSpaceOnUse");
			patternNode.setAttribute("width", String.valueOf(IMAGE_WIDTH) );
			patternNode.setAttribute("height", String.valueOf(IMAGE_HEIGHT));
			
			final Element imageNode = doc.createElement("image");
			imageNode.setAttribute("x", "0");
			imageNode.setAttribute("y", "0");
			imageNode.setAttribute("width", String.valueOf(IMAGE_WIDTH));
			imageNode.setAttribute("height", String.valueOf(IMAGE_HEIGHT));
			
			if(EMBED_IMAGE_IN_SVG){
				//xlink:href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg=="
				final BufferedImage image = images.get(constituency);
				final String imgBase64Data;
				try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
					// embed image data as base64 GIF
					ImageIO.write(image, "gif", byteArrayOutputStream);
					imgBase64Data = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
				}
				
				imageNode.setAttribute("xlink:href", "data:image/gif;base64," + imgBase64Data);
			}else{
				//  xlink:href=".\\constituencies\${ElectionYearDataSource}\Aberavon.png"
				final String imagePath = ".\\" + electionData.getElectionYearDataSource() + TARGET_OUTPUT_IMAGE_DIR + constituencyNameKey + TARGET_OUTPUT_IMAGE_FORMAT;
				imageNode.setAttribute("xlink:href", imagePath);
			}
			
			patternNode.appendChild(imageNode);
			defsNode.appendChild(patternNode);
		}
	}

	/**
	 * Add XLink namespace to XML document
	 * xmlns:xlink="http://www.w3.org/1999/xlink"
	 */
	private static void addXlinkNamespace(final Document doc) {
		final Element root = doc.getDocumentElement();
		final Attr attr = doc.createAttribute("xmlns:xlink");
		attr.setValue("http://www.w3.org/1999/xlink");
		root.setAttributeNodeNS(attr);
	}

	/**
	 * Generate the constituency voting distribution images
	 */
	private static Map<String, BufferedImage> generateImages(final Map<String, Candidates> constituencyParties, final ElectionData electionData) throws InterruptedException {
		final Map<String, BufferedImage> images = new ConcurrentHashMap<>();
		final ExecutorService imageGenerationThreadPool = Executors.newFixedThreadPool(NUM_IMAGE_GENERATION_THREADS);
		final long startTime = System.currentTimeMillis();
		
		// create output directory if it doesn't yet exists + clean previous output
		final File mapImageDir = new File(TARGET_OUTPUT_BASE_DIR + electionData.getElectionYearDataSource() + TARGET_OUTPUT_IMAGE_DIR);
		mapImageDir.mkdirs();
		for(File imageFile : mapImageDir.listFiles()) {
			final var deleted = imageFile.delete();
			if(!deleted){
				System.err.println("Failed to delete file: " + imageFile);
			}
		}
		
		for (Entry<String, Candidates> entry : constituencyParties.entrySet()) {

			imageGenerationThreadPool.execute( () -> {
				final String imgName = entry.getKey();
				System.out.println("Generate image [imgName=" + imgName + "]");
				final BufferedImage image = generateImage( new ConstituencyVoteDistributionImageGeneratorExact( entry.getValue() ) );
				final File outputImagePath = new File(mapImageDir, electionData.getConstituencyKeyGenerator().toKey(imgName) + "." + TARGET_OUTPUT_IMAGE_FORMAT);
				try {
					ImageIO.write(image, TARGET_OUTPUT_IMAGE_FORMAT, outputImagePath);
				} catch (Exception e) {
					System.err.println("Problem writing image " + e);
				}
				images.put(imgName, image);
			});
			
		}

		// clean shutdown of thread pool tasks
		imageGenerationThreadPool.shutdown();
		final boolean finished = imageGenerationThreadPool.awaitTermination(10, TimeUnit.MINUTES);
		if(!finished)
			System.err.println("Image generation still running after 10 minutes");
		else{
			System.out.println("Image generation finished [imageCount=" + images.size() + ", durationSeconds=" + (System.currentTimeMillis() - startTime)/1000 + "]");
		}
		
		return images;
	}

	private static BufferedImage generateImage(final ImageGenerator imageGenerator) {
		final BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
		imageGenerator.generate( image.getRaster() );
		return image;
	}
}