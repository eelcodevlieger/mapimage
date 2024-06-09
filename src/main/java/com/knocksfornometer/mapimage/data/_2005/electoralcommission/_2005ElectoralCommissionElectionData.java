package com.knocksfornometer.mapimage.data._2005.electoralcommission;

import java.util.Map;

import com.knocksfornometer.mapimage.domain.Candidates;
import com.knocksfornometer.mapimage.data.electoralcommission.ElectoralCommissionElectionData;

/**
 * 
 * @author Eelco de Vlieger
 */
public class _2005ElectoralCommissionElectionData {

	private static final String INPUT_DATA_FILE = "src\\main\\resources\\election_data\\2005\\electoral_commission\\Generalelection2005_A-Zconstituencyresults_18784-13893__E__N__S__W__.xls";
	
	public static Map<String, Candidates> loadElectionData(Map<String, String> partyColorMapping) throws Exception {
		return ElectoralCommissionElectionData.loadElectionData(INPUT_DATA_FILE, partyColorMapping, false, 11);
	}
}