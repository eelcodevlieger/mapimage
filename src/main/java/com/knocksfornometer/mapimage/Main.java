package com.knocksfornometer.mapimage;

import static com.knocksfornometer.mapimage.xml.XmlUtils.loadAsXmlDocument;
import static com.knocksfornometer.mapimage.xml.XmlUtils.writeXmlDocumentToFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.knocksfornometer.mapimage.data.ElectionData;
import com.knocksfornometer.mapimage.data.ElectionDataManager;
import com.knocksfornometer.mapimage.data.ElectionYearDataSource;
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

	private static final int NUM_IMAGE_GENERATION_THREADS = 10;

	private static final File RESOURCES_DIRECTORY = new File("src\\main\\resources");
	private static final String SVG_MAP_INPUT_FILE = "2015UKElectionMap.svg";
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
	private static final boolean EMBED_IMAGE_IN_SVG = false;

	
	public static void main(String[] args) throws Exception {
		for ( ElectionYearDataSource electionYearDataSource : ElectionYearDataSource.values() ) {
			System.out.println("Loading election data [electionYearDataSource=" + electionYearDataSource + "]");
			
			ElectionData electionData = ElectionDataManager.getElectionData(electionYearDataSource);
			if(electionData == null){
				System.out.println("No election data for electionYearDataSource [electionYearDataSource=" + electionYearDataSource + "]");
				continue;
			}
			
			processElectionData(electionData, electionYearDataSource);
		}
		
		System.out.println("Completed");
	}

	private static void processElectionData(ElectionData electionData, ElectionYearDataSource electionYearDataSource) throws Exception {
		System.out.println("generate a 'voting distribution' image for each constituency [electionYearDataSource=" + electionYearDataSource + "]");
		Map<String, BufferedImage> images = generateImages(electionData.getElectionDataMap(), electionData);

		System.out.println("update SVG map image and save the resulting file [electionYearDataSource=" + electionYearDataSource + "]");
		processSvg(images, electionData);
	}

	/**
	 * Read SVG -> update constituency images -> write SVG
	 * @param electionData 
	 */
	private static void processSvg(Map<String, BufferedImage> images, ElectionData electionData) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, TransformerException {
		Map<String, String> constituencyKeyNameToImageMap = images.keySet().stream().collect( Collectors.toMap(electionData.getConstituencyKeyGenerator()::toKey, Function.identity()) );
		
		Document doc = loadAsXmlDocument( new File(RESOURCES_DIRECTORY, SVG_MAP_INPUT_FILE) );

		XPath xpath = XPathFactory.newInstance().newXPath();

		Set<String> matches = new HashSet<>();
		Set<String> noMatch = new TreeSet<>();

		System.out.println("SVG :: link constituency paths to images");
		linkConstituencyPathsToImages(constituencyKeyNameToImageMap, doc, xpath, matches, noMatch, electionData.getConstituencyKeyGenerator());

		addXlinkNamespace(doc);

		System.out.println("SVG :: generate pattern image definitions");
		generatePatternImageDefs(doc, xpath, matches, images, electionData);
		
		System.out.println("constituency names matching results [match=" + matches.size() + ", noMatch=" + noMatch.size() + "]");
		if(noMatch.size() > 0)
			System.out.println("noMatch=" + noMatch);

		System.out.println("SVG :: save updated file");
		ElectionYearDataSource source = electionData.getElectionYearDataSource();
		writeXmlDocumentToFile(doc, TARGET_OUTPUT_BASE_DIR + source + "\\" + source.getElectionYear().getYear() + SVG_MAP_OUTPUT_FILE);
	}

	/**
	 * Links constituency paths to the generated images 
	 */
	private static void linkConstituencyPathsToImages(	Map<String, String> constituencyKeyToImageName,
														Document doc,
														XPath xpath,
														Set<String> matches,
														Set<String> noMatch,
														ConstituencyKeyGenerator constituencyKeyGenerator) throws XPathExpressionException {

		// specific 'hack' to remove a rogue path from the input SVG
		Node pathToRemove = ((NodeList) xpath.compile("//*[@id='path717']").evaluate(doc, XPathConstants.NODESET)).item(0);
		pathToRemove.getParentNode().removeChild(pathToRemove);
		
		// Iterate over all constituency SVG paths using XPath to select the DOM nodes
		NodeList pathnodes = ((NodeList) xpath.compile("//path").evaluate(doc, XPathConstants.NODESET));
		for (int i = 0; i < pathnodes.getLength(); i++) {
			Node pathNode = pathnodes.item(i);
			NamedNodeMap attributes = pathNode.getAttributes();
			Node constituencyAttribute = attributes.getNamedItem("id");
			String constituency = constituencyAttribute.getNodeValue();

			if(constituency.startsWith("path"))
				continue; // only care about names constituencies

			String constituencyKey = constituencyKeyGenerator.toKey(constituency);
			String sourceConstituency = constituencyKeyToImageName.get( constituencyKey );
			
			// Update the style attribute to link to the images by ID
			String style = "fill:url(#" + constituencyKey + ")";
			Node styleAttribute = attributes.getNamedItem("style");
			if( sourceConstituency != null ){
				matches.add(sourceConstituency);

				if(styleAttribute!= null){
					String originalStyle = styleAttribute.getNodeValue();
					int semicolonIndex = originalStyle.indexOf(";");
					if(semicolonIndex >= 0)
						style += originalStyle.substring(semicolonIndex);
					style = style.replaceAll("&", "&amp;");
					// increase the constituency border line width
					style = style.replace("stroke-width:0.5", "stroke-width:0.8");
				}else{
					// if no style exists, add it
					styleAttribute = pathNode.getOwnerDocument().createAttribute("style");
				    attributes.setNamedItem(styleAttribute);
				}
			}else{
				noMatch.add(constituencyKey);
				if(styleAttribute!= null){
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
	private static void generatePatternImageDefs(	Document doc,
													XPath xpath,
													Set<String> matches,
													Map<String, BufferedImage> images,
													ElectionData electionData) throws XPathExpressionException, IOException {
		NodeList nodes = (NodeList) xpath.compile("//defs").evaluate(doc, XPathConstants.NODESET);
		Node defsNode = nodes.item(0);
		for (String constituency : matches) {
			// <pattern id="Aberavon" patternUnits="userSpaceOnUse" height="100" width="100">
			//    <image x="0" y="0" height="100" width="100" xlink:href=".\constituencies\Aberavon.png"></image>
			// </pattern>
			Element patternNode = doc.createElement("pattern");
			String constituencyNameKey = electionData.getConstituencyKeyGenerator().toKey(constituency);
			patternNode.setAttribute("id", constituencyNameKey);
			patternNode.setAttribute("patternUnits", "userSpaceOnUse");
			patternNode.setAttribute("width", String.valueOf(IMAGE_WIDTH) );
			patternNode.setAttribute("height", String.valueOf(IMAGE_HEIGHT));
			
			Element imageNode = doc.createElement("image");
			imageNode.setAttribute("x", "0");
			imageNode.setAttribute("y", "0");
			imageNode.setAttribute("width", String.valueOf(IMAGE_WIDTH));
			imageNode.setAttribute("height", String.valueOf(IMAGE_HEIGHT));
			
			if(EMBED_IMAGE_IN_SVG){
				//  xlink:href=".\\constituencies\${ElectionYearDataSource}\Aberavon.png"
				String imagePath = ".\\" + electionData.getElectionYearDataSource() + TARGET_OUTPUT_IMAGE_DIR + constituencyNameKey + TARGET_OUTPUT_IMAGE_FORMAT;
				imageNode.setAttribute("xlink:href", imagePath);
			}else{
				//xlink:href="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg=="
				BufferedImage image = images.get(constituency);
				String imgBase64Data;
				try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
					// embed image data as base64 GIF
					ImageIO.write(image, "gif", byteArrayOutputStream);
					imgBase64Data = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
				}
				
				imageNode.setAttribute("xlink:href", "data:image/gif;base64," + imgBase64Data);
			}
			
			patternNode.appendChild(imageNode);
			defsNode.appendChild(patternNode);
		}
	}

	/**
	 * Add XLink namespace to XML document
	 * xmlns:xlink="http://www.w3.org/1999/xlink"
	 */
	private static void addXlinkNamespace(Document doc) {
		Element root = doc.getDocumentElement();
		Attr attr = doc.createAttribute("xmlns:xlink");
		attr.setValue("http://www.w3.org/1999/xlink");
		root.setAttributeNodeNS(attr);
	}

	/**
	 * Generate the constituency voting distribution images
	 */
	private static Map<String, BufferedImage> generateImages(Map<String, Candidates> constituencyParties, ElectionData electionData) throws InterruptedException {
		Map<String, BufferedImage> images = new ConcurrentHashMap<>();
		
		long startTime = System.currentTimeMillis();
		
		ExecutorService imageGenerationThreadPool = Executors.newFixedThreadPool(NUM_IMAGE_GENERATION_THREADS);
		
		// create output directory if it doesn't yet exists + clean previous output
		File mapImageDir = new File(TARGET_OUTPUT_BASE_DIR + electionData.getElectionYearDataSource() + TARGET_OUTPUT_IMAGE_DIR);
		mapImageDir.mkdirs();
		for(File imageFile: mapImageDir.listFiles()) 
			imageFile.delete(); 
		
		for (Entry<String, Candidates> entry : constituencyParties.entrySet()) {

			imageGenerationThreadPool.execute( new Runnable() {
				@Override
				public void run() {
					String imgName = entry.getKey();
					System.out.println("Generate image [imgName=" + imgName + "]");
					BufferedImage image = generateImage( new ConstituencyVoteDistributionImageGeneratorExact( entry.getValue() ) );
					File outputImagePath = new File(mapImageDir, electionData.getConstituencyKeyGenerator().toKey(imgName) + "." + TARGET_OUTPUT_IMAGE_FORMAT);
					try {
						ImageIO.write(image, TARGET_OUTPUT_IMAGE_FORMAT, outputImagePath);
					} catch (Exception e) {
						System.err.println("Problem writing image " + e);
					}
					images.put(imgName, image);
				}
			});
			
		}

		// clean shutdown of thread pool tasks
		imageGenerationThreadPool.shutdown();
		boolean finished = imageGenerationThreadPool.awaitTermination(10, TimeUnit.MINUTES);
		if(!finished)
			System.err.println("Image generation still running after 10 minutes");
		else{
			System.out.println("Image generation finished [imageCount=" + images.size() + ", durationSeconds=" + (System.currentTimeMillis() - startTime)/1000 + "]");
		}
		
		return images;
	}

	private static BufferedImage generateImage(ImageGenerator imageGenerator) {
		BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
		imageGenerator.generate( image.getRaster() );
		return image;
	}
}