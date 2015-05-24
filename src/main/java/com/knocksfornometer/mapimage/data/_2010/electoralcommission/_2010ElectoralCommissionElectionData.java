package com.knocksfornometer.mapimage.data._2010.electoralcommission;

import java.util.Map;

import com.knocksfornometer.mapimage.Candidates;
import com.knocksfornometer.mapimage.data.electoralcommission.ElectoralCommissionElectionData;

/**
 * 
 * @author Eelco de Vlieger
 */
public class _2010ElectoralCommissionElectionData {

	private static final String INPUT_DATA_FILE = "src\\main\\resources\\election_data\\2010\\electoral_commission\\GE2010-constituency-results-website.xls";
	
	public static Map<String, Candidates> loadElectionData(Map<String, String> partyColorMapping) throws Exception {
		return ElectoralCommissionElectionData.loadElectionData(INPUT_DATA_FILE, partyColorMapping, true, 1);
	}
}