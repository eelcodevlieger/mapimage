package com.github.mapimage.data._2019.ukparliament;

import com.github.mapimage.data.ElectionDataLoader;
import com.github.mapimage.data.source.ukparliament.UkParliamentElectionData;
import com.github.mapimage.domain.Candidates;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.github.mapimage.data.ElectionYearDataSource.*;

@Slf4j
public class _2019UkParliamentElectionData implements ElectionDataLoader {

	/** using result 'by candidates' file, rather than 'by constituency', because the latter doesn't detail the smaller political parties */
	private static final String INPUT_DATA_FILE_RESULTS_BY_CANDIDATE = "HoC-GE2019-results-by-candidate-xlsx.xlsx";
	private static final String INPUT_DATA_FILE_RESULTS_BY_CONSTITUENCY = "HoC-GE2019-results-by-constituency-xlsx.xlsx";

	public Map<String, Candidates> apply(final Map<String, String> partyColorMapping) {
		return UkParliamentElectionData.loadElectionData(
				_2019UkParliament,
				INPUT_DATA_FILE_RESULTS_BY_CANDIDATE,
				INPUT_DATA_FILE_RESULTS_BY_CONSTITUENCY,
				partyColorMapping,
				0,
				8,
				14,
				14);
	}
}