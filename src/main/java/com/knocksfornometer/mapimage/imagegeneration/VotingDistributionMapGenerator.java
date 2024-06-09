package com.knocksfornometer.mapimage.imagegeneration;

import com.knocksfornometer.mapimage.domain.ConstituencyKeyGenerator;
import com.knocksfornometer.mapimage.domain.ElectionData;
import lombok.AllArgsConstructor;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.knocksfornometer.mapimage.utils.XmlUtils.loadAsXmlDocument;

/**
 * Generates an SVG map image of the UK - displaying the coting distribution of each constituency area.
 */
@AllArgsConstructor
public class VotingDistributionMapGenerator {
    private static final File RESOURCES_DIRECTORY = new File("src\\main\\resources");

    private final String targetOutputImageDir;
    private final String targetOutputImageFormat;
    private final int imageWidth;
    private final int imageHeight;
    /** controls if the image data gets embedded directly in the svg file, or if it gets linked to external image files */
    private final boolean embedImageInSvg;

    public Document generate(Map<String, BufferedImage> images, ElectionData electionData) throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        final Map<String, String> constituencyKeyNameToImageMap = images.keySet().stream().collect( Collectors.toMap(electionData.constituencyKeyGenerator()::toKey, Function.identity()) );

        final Document svgMapDocument = loadAsXmlDocument( new File(RESOURCES_DIRECTORY, electionData.svgMapInputFile()) );

        final XPath xpath = XPathFactory.newInstance().newXPath();

        final Set<String> matches = new HashSet<>();
        final Set<String> noMatch = new TreeSet<>();

        clean(xpath, svgMapDocument);

        System.out.println("SVG :: link constituency paths to images");
        linkConstituencyPathsToImages(constituencyKeyNameToImageMap, svgMapDocument, xpath, matches, noMatch, electionData.constituencyKeyGenerator());

        addXlinkNamespace(svgMapDocument);

        System.out.println("SVG :: generate pattern image definitions");
        generatePatternImageDefs(svgMapDocument, xpath, matches, images, electionData);

        System.out.println("constituency names matching results [match=" + matches.size() + ", noMatch=" + noMatch.size() + "]");
        if(noMatch.size() > 0)
            System.out.println("noMatch=" + noMatch);

        return svgMapDocument;
    }

    private static void clean(XPath xpath, Document svgMapDocument) throws XPathExpressionException {
        // specific 'hack' to remove a rogue path from the input SVG
        final Node pathToRemove = ((NodeList) xpath.compile("//*[@id='path717']").evaluate(svgMapDocument, XPathConstants.NODESET)).item(0);
        if(pathToRemove != null)
            pathToRemove.getParentNode().removeChild(pathToRemove);
    }

    /**
     * Links constituency paths to the generated images
     */
    private void linkConstituencyPathsToImages(final Map<String, String> constituencyKeyToImageName,
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
     */
    private void generatePatternImageDefs(final Document doc,
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

        var imgWidth = String.valueOf(imageWidth);
        var imgHeight = String.valueOf(imageHeight);

        for (String constituency : matches) {
            final Element patternNode = createSvgPattern(doc, images, electionData, constituency, imgWidth, imgHeight);
            defsNode.appendChild(patternNode);
        }
    }

    /**
     * Example pattern element node
     * {@literal
     *   <pattern id="Aberavon" patternUnits="userSpaceOnUse" height="100" width="100" patternTransform="scale(0.2)">
     *       <image x="0" y="0" height="100" width="100" xlink:href=".\constituencies\Aberavon.png"></image>
     *   </pattern>
     * }
     */
    private Element createSvgPattern(Document doc, Map<String, BufferedImage> images, ElectionData electionData, String constituency, String imgWidth, String imgHeight) throws IOException {
        final String constituencyNameKey = electionData.constituencyKeyGenerator().toKey(constituency);

        final Element patternNode = doc.createElement("pattern");
        patternNode.setAttribute("id", constituencyNameKey);
        patternNode.setAttribute("patternUnits", "userSpaceOnUse");
        patternNode.setAttribute("width", imgWidth);
        patternNode.setAttribute("height", imgHeight);
        patternNode.setAttribute("patternTransform", "scale(0.2)");

        final Element imageNode = doc.createElement("image");
        imageNode.setAttribute("x", "0");
        imageNode.setAttribute("y", "0");
        imageNode.setAttribute("width", imgWidth);
        imageNode.setAttribute("height", imgHeight);

        if(embedImageInSvg){
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
            final String imagePath = ".\\" + electionData.electionYearDataSource() + targetOutputImageDir + constituencyNameKey + targetOutputImageFormat;
            imageNode.setAttribute("xlink:href", imagePath);
        }

        patternNode.appendChild(imageNode);
        return patternNode;
    }

    /**
     * Add XLink namespace to XML document
     * xmlns:xlink="http://www.w3.org/1999/xlink"
     */
    private void addXlinkNamespace(final Document doc) {
        final Attr attr = doc.createAttribute("xmlns:xlink");
        attr.setValue("http://www.w3.org/1999/xlink");
        doc.getDocumentElement().setAttributeNodeNS(attr);
    }
}
