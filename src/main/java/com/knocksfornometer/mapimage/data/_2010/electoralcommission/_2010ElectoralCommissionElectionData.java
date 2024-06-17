package com.knocksfornometer.mapimage.data._2010.electoralcommission;

import java.util.Map;

import com.knocksfornometer.mapimage.data.ElectionDataLoader;
import com.knocksfornometer.mapimage.domain.Candidates;
import com.knocksfornometer.mapimage.data.electoralcommission.ElectoralCommissionElectionData;
import lombok.SneakyThrows;

/**
 * 
 * @author Eelco de Vlieger
 */
public class _2010ElectoralCommissionElectionData implements ElectionDataLoader {

	private static final String INPUT_DATA_FILE = "src\\main\\resources\\election_data\\2010\\electoral_commission\\GE2010-constituency-results-website.xls";
	
	@SneakyThrows
	public Map<String, Candidates> apply(Map<String, String> partyColorMapping){
		return ElectoralCommissionElectionData.loadElectionData(INPUT_DATA_FILE, partyColorMapping, true, 1);
	}
}