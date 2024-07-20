package com.knocksfornometer.mapimage.data._2005.electoralcommission;

import java.util.Map;

import com.knocksfornometer.mapimage.data.ElectionDataLoader;
import com.knocksfornometer.mapimage.domain.Candidates;
import com.knocksfornometer.mapimage.data.source.electoralcommission.ElectoralCommissionElectionData;
import lombok.SneakyThrows;

import static com.knocksfornometer.mapimage.data.ElectionYearDataSource.*;

public class _2005ElectoralCommissionElectionData implements ElectionDataLoader {

	private static final String INPUT_DATA_FILE = "Generalelection2005_A-Zconstituencyresults_18784-13893__E__N__S__W__.xls";
	
	@SneakyThrows
	public Map<String, Candidates> apply(final Map<String, String> partyColorMapping) {
		return ElectoralCommissionElectionData.loadElectionData(_2005ElectoralCommission, INPUT_DATA_FILE, partyColorMapping, false, 11);
	}
}