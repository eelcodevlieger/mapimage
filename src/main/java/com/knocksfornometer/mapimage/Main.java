package com.knocksfornometer.mapimage;

import com.google.common.base.CaseFormat;
import com.knocksfornometer.mapimage.domain.ConstituencyMapping;
import com.knocksfornometer.mapimage.domain.ElectionData;
import com.knocksfornometer.mapimage.data.ElectionYearDataSource;
import com.knocksfornometer.mapimage.imagegeneration.ImageGenerator;
import com.knocksfornometer.mapimage.imagegeneration.VotingDistributionImagesGenerator;
import com.knocksfornometer.mapimage.imagegeneration.VotingDistributionMapGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static com.knocksfornometer.mapimage.utils.JsonUtils.loadStringMapFromJsonFile;
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
	private static final int IMAGE_WIDTH = 300;
	private static final int IMAGE_HEIGHT = 300;

	private static final String[] PREFIXES = {"CITYOF", "THE", "MID", "CENTRAL", "NORTH", "EAST", "SOUTH", "WEST"};
	private static final File RESOURCES_DIRECTORY = new File("src\\main\\resources");
	private static final String CONSTITUENCY_NAME_MAPPING_FILE = "constituency_name_mapping.json";
	private static final String SEAT_TO_CONSTITUENCY_NAME_MAPPING_FILE = "2005_seat_to_constituency_mapping.json";

	/**
	 * If true  - the image data gets embedded directly in the SVG file (base64 encoded).
	 * If false - the images get linked as external files.
	 */
	private static final boolean EMBED_IMAGE_IN_SVG = true;

	
	public static void main(final String... args) throws Exception {
		for(ElectionYearDataSource electionYearDataSource : ElectionYearDataSource.getElectionYearDataSources(args)) {
			System.out.println("Loading election data [electionYearDataSource=" + electionYearDataSource + "]");
			var electionData = getElectionData(electionYearDataSource);

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

	private static ElectionData getElectionData(ElectionYearDataSource electionYearDataSource) throws IOException {
		var constituencyNameMapping = loadStringMapFromJsonFile( new File(RESOURCES_DIRECTORY, CONSTITUENCY_NAME_MAPPING_FILE) );
		var seatNumberToConstituencyNameMapping = loadStringMapFromJsonFile( new File(RESOURCES_DIRECTORY, SEAT_TO_CONSTITUENCY_NAME_MAPPING_FILE) );
		var constituencyMapping = new ConstituencyMapping(PREFIXES, constituencyNameMapping, seatNumberToConstituencyNameMapping);
		var partyColorMapping = getPartyColorMapping(electionYearDataSource);
		var svgMapInputFile = getSvgMapInputFile(electionYearDataSource.getElectionYear().getYear());
		return new ElectionData(electionYearDataSource, partyColorMapping, svgMapInputFile, constituencyMapping);
	}

	private static String getSvgMapInputFile(final int year) {
		return "%dUKElectionMap.svg".formatted(year);
	}

	private static Map<String, String> getPartyColorMapping(final ElectionYearDataSource electionYearDataSource) throws IOException {
		return loadStringMapFromJsonFile(new File(RESOURCES_DIRECTORY,
				"election_data\\%d\\%s\\party_color_mapping.json".formatted(
						electionYearDataSource.getElectionYear().getYear(),
						CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, electionYearDataSource.getElectionDataSource().name()))));
	}
}