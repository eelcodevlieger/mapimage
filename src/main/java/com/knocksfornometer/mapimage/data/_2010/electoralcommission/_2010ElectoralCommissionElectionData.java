package com.knocksfornometer.mapimage.data._2010.electoralcommission;

import java.util.Map;

import com.knocksfornometer.mapimage.data.ElectionDataLoader;
import com.knocksfornometer.mapimage.domain.Candidates;
import com.knocksfornometer.mapimage.data.source.electoralcommission.ElectoralCommissionElectionData;
import lombok.SneakyThrows;

import static com.knocksfornometer.mapimage.data.ElectionYearDataSource._2010ElectoralCommission;

public class _2010ElectoralCommissionElectionData implements ElectionDataLoader {

	private static final String INPUT_DATA_FILE = "GE2010-constituency-results-website.xls";
	
	@SneakyThrows
	public Map<String, Candidates> apply(final Map<String, String> partyColorMapping){
		return ElectoralCommissionElectionData.loadElectionData(_2010ElectoralCommission, INPUT_DATA_FILE, partyColorMapping, true, 1);
	}
}