package com.knocksfornometer.mapimage.data;

import static com.knocksfornometer.mapimage.json.JsonUtils.loadStringMapFromJsonFile;

import java.io.File;
import java.util.Map;

import com.knocksfornometer.mapimage.data._2005.electoralcommission._2005ElectoralCommissionElectionData;
import com.knocksfornometer.mapimage.data._2010.electoralcommission._2010ElectoralCommissionElectionData;
import com.knocksfornometer.mapimage.data._2015.electoralcommission._2015ElectoralCommissionElectionData;
import com.knocksfornometer.mapimage.data._2017.electoralcommission._2017ElectoralCommissionElectionData;
import com.knocksfornometer.mapimage.data._2019.ukparliament._2019UkParliamentElectionData;

public class ElectionDataManager {
	
	private static final String[] PREFIXES = {"CITYOF", "THE", "MID", "CENTRAL", "NORTH", "EAST", "SOUTH", "WEST"};
	private static final File RESOURCES_DIRECTORY = new File("src\\main\\resources");
	private static final String CONSTITUENCY_NAME_MAPPING_FILE = "constituency_name_mapping.json";
	private static final String SEAT_TO_CONSTITUENCY_NAME_MAPPING_FILE = "2005_seat_to_constituency_mapping.json";
	private static final String PARTY_COLOR_MAPPING_FILE_ELECTORAL_COMMISSION_2010 = "election_data\\2010\\electoral_commission\\party_color_mapping.json";
	private static final String PARTY_COLOR_MAPPING_FILE_ELECTORAL_COMMISSION_2015 = "election_data\\2015\\electoral_commission\\party_color_mapping.json";
	private static final String PARTY_COLOR_MAPPING_FILE_ELECTORAL_COMMISSION_2017 = "election_data\\2017\\electoral_commission\\party_color_mapping.json";
	private static final String PARTY_COLOR_MAPPING_FILE_UK_PARLIAMENT_2019 = "election_data\\2019\\uk_parliament\\party_color_mapping.json";
	private static final String _2005_SVG_MAP_INPUT_FILE = "2005UKElectionMap.svg";
	private static final String _2010_SVG_MAP_INPUT_FILE = "2010UKElectionMap.svg";
	private static final String _2015_SVG_MAP_INPUT_FILE = "2015UKElectionMap.svg";
    @SuppressWarnings("unused")
    private static final String _2017_SVG_MAP_INPUT_FILE = "2017UKElectionMap.svg";
    private static final String _2019_SVG_MAP_INPUT_FILE = "2019UKElectionMap.svg";

	public static ElectionData getElectionData(ElectionYearDataSource electionYearDataSource) throws Exception{
		final Map<String, String> constituencyNameMapping = loadStringMapFromJsonFile( new File(RESOURCES_DIRECTORY, CONSTITUENCY_NAME_MAPPING_FILE) );
		final Map<String, String> seatNumberToConstituencyNameMapping = loadStringMapFromJsonFile( new File(RESOURCES_DIRECTORY, SEAT_TO_CONSTITUENCY_NAME_MAPPING_FILE) );
		final ConstituencyMapping constituencyMapping = new ConstituencyMapping(PREFIXES, constituencyNameMapping, seatNumberToConstituencyNameMapping);

		switch (electionYearDataSource) {
			case _2005ElectoralCommission -> {
				final Map<String, String> partyColorMapping = loadStringMapFromJsonFile(new File(RESOURCES_DIRECTORY, PARTY_COLOR_MAPPING_FILE_ELECTORAL_COMMISSION_2010));
				return new ElectionData(electionYearDataSource, _2005ElectoralCommissionElectionData.loadElectionData(partyColorMapping), _2005_SVG_MAP_INPUT_FILE, constituencyMapping);
			}
			case _2010ElectoralCommission -> {
				final Map<String, String> partyColorMapping = loadStringMapFromJsonFile(new File(RESOURCES_DIRECTORY, PARTY_COLOR_MAPPING_FILE_ELECTORAL_COMMISSION_2010));
				return new ElectionData(electionYearDataSource, _2010ElectoralCommissionElectionData.loadElectionData(partyColorMapping), _2010_SVG_MAP_INPUT_FILE, constituencyMapping);
			}
			case _2015ElectoralCommission -> {
				final Map<String, String> partyColorMapping = loadStringMapFromJsonFile(new File(RESOURCES_DIRECTORY, PARTY_COLOR_MAPPING_FILE_ELECTORAL_COMMISSION_2015));
				return new ElectionData(electionYearDataSource, _2015ElectoralCommissionElectionData.loadElectionData(partyColorMapping), _2015_SVG_MAP_INPUT_FILE, constituencyMapping);
			}
			case _2017ElectoralCommission -> {
				final Map<String, String> partyColorMapping = loadStringMapFromJsonFile(new File(RESOURCES_DIRECTORY, PARTY_COLOR_MAPPING_FILE_ELECTORAL_COMMISSION_2017));
				return new ElectionData(electionYearDataSource, _2017ElectoralCommissionElectionData.loadElectionData(partyColorMapping), _2017_SVG_MAP_INPUT_FILE, constituencyMapping);
			}
			case _2019UkParliament -> {
				final Map<String, String> partyColorMapping = loadStringMapFromJsonFile(new File(RESOURCES_DIRECTORY, PARTY_COLOR_MAPPING_FILE_UK_PARLIAMENT_2019));
				return new ElectionData(electionYearDataSource, _2019UkParliamentElectionData.loadElectionData(partyColorMapping), _2019_SVG_MAP_INPUT_FILE, constituencyMapping);
			}
			default ->
					throw new RuntimeException("Unmapped electionYearDataSource [electionYearDataSource=" + electionYearDataSource + "]");
		}
	}
}