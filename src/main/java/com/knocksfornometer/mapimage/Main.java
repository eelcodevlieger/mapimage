package com.knocksfornometer.mapimage;

import com.knocksfornometer.mapimage.domain.ElectionData;
import com.knocksfornometer.mapimage.data.ElectionDataManager;
import com.knocksfornometer.mapimage.data.ElectionYearDataSource;
import com.knocksfornometer.mapimage.imagegeneration.ImageGenerator;
import com.knocksfornometer.mapimage.imagegeneration.VotingDistributionImagesGenerator;
import com.knocksfornometer.mapimage.imagegeneration.VotingDistributionMapGenerator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import static com.knocksfornometer.mapimage.utils.XmlUtils.writeXmlDocumentToFile;

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
		for(ElectionYearDataSource electionYearDataSource : ElectionYearDataSource.getElectionYearDataSources(args)) {
			System.out.println("Loading election data [electionYearDataSource=" + electionYearDataSource + "]");
			var electionData = ElectionDataManager.getElectionData(electionYearDataSource);

			System.out.println("Generate a 'voting distribution' image for each constituency [electionYearDataSource=" + electionYearDataSource + "]");
			var votingDistributionImagesGenerator = new VotingDistributionImagesGenerator(TARGET_OUTPUT_BASE_DIR, TARGET_OUTPUT_IMAGE_DIR, TARGET_OUTPUT_IMAGE_FORMAT, IMAGE_WIDTH, IMAGE_HEIGHT);
			var patternImages = votingDistributionImagesGenerator.generateImages(electionData);

			System.out.println("Update SVG map image and save the resulting file [electionYearDataSource=" + electionYearDataSource + "]");
			var votingDistributionMapGenerator = new VotingDistributionMapGenerator(TARGET_OUTPUT_IMAGE_DIR, TARGET_OUTPUT_IMAGE_FORMAT, IMAGE_WIDTH, IMAGE_HEIGHT, EMBED_IMAGE_IN_SVG);
			var votingDistributionMap = votingDistributionMapGenerator.generate(patternImages, electionData);;

			System.out.println("Save generate voting distribution map to file");
			writeXmlDocumentToFile(votingDistributionMap, TARGET_OUTPUT_BASE_DIR + electionData.electionYearDataSource() + "\\" + electionData.electionYearDataSource().getElectionYear().getYear() + SVG_MAP_OUTPUT_FILE);
		}
		
		System.out.println("Completed");
	}
}