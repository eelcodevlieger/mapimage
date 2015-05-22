package com.knocksfornometer.mapimage.data;

import static com.knocksfornometer.mapimage.json.JsonUtils.loadStringMapFromJsonFile;

import java.util.Map;

import com.knocksfornometer.mapimage.data._2010.electoralcommission._2010ElectoralCommissionElectionData;
import com.knocksfornometer.mapimage.data._2015.guardian.ConstituencyMapping;
import com.knocksfornometer.mapimage.data._2015.guardian._2015GuardianElectionData;

public class ElectionDataManager {
	// TODO to properties file
	private static final String[] PREFIXES = {"CITYOF", "THE", "MID", "CENTRAL", "NORTH", "EAST", "SOUTH", "WEST"};
	private static final String CONSTITUENCY_NAME_MAPPING_FILE = "src\\main\\resources\\election_data\\2015\\guardian\\constituency_name_mapping.json";
	private static final String PARTY_COLOR_MAPPING_FILE_GUARDIAN = "src\\main\\resources\\election_data\\2015\\guardian\\party_color_mapping.json";
	private static final String PARTY_COLOR_MAPPING_FILE_ELECTORAL_COMMISSION = "src\\main\\resources\\election_data\\2010\\electoral_commission\\party_color_mapping.json";
	
	public static ElectionData getElectionData(ElectionYearDataSource electionYearDataSource) throws Exception{
		Map<String, String> constituencyNameMapping = loadStringMapFromJsonFile(CONSTITUENCY_NAME_MAPPING_FILE);
		ConstituencyMapping constituencyMapping = new ConstituencyMapping(PREFIXES, constituencyNameMapping);
		
		switch (electionYearDataSource) {
			case _2010ElectoralCommission: {
				Map<String, String> partyColorMapping = loadStringMapFromJsonFile(PARTY_COLOR_MAPPING_FILE_ELECTORAL_COMMISSION);
				return new ElectionData(electionYearDataSource, _2010ElectoralCommissionElectionData.loadElectionData(partyColorMapping), constituencyMapping);
			}
			case _2015ElectoralCommission: {
				return null; // not yet implemented
			}
			case _2015Guardian: {
				Map<String, String> partyColorMapping = loadStringMapFromJsonFile(PARTY_COLOR_MAPPING_FILE_GUARDIAN);
				return new ElectionData(electionYearDataSource, _2015GuardianElectionData.loadElectionData(partyColorMapping, constituencyMapping), constituencyMapping);
			}
			default:
				throw new RuntimeException("Unmapped electionYearDataSource [electionYearDataSource=" + electionYearDataSource + "]");
		}
	}
}