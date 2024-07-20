package com.github.mapimage.data._2010.electoralcommission;

import java.util.Map;

import com.github.mapimage.data.ElectionDataLoader;
import com.github.mapimage.domain.Candidates;
import com.github.mapimage.data.source.electoralcommission.ElectoralCommissionElectionData;
import lombok.SneakyThrows;

import static com.github.mapimage.data.ElectionYearDataSource._2010ElectoralCommission;

public class _2010ElectoralCommissionElectionData implements ElectionDataLoader {

	private static final String INPUT_DATA_FILE = "GE2010-constituency-results-website.xls";
	
	@SneakyThrows
	public Map<String, Candidates> apply(final Map<String, String> partyColorMapping){
		return ElectoralCommissionElectionData.loadElectionData(_2010ElectoralCommission, INPUT_DATA_FILE, partyColorMapping, true, 1);
	}
}