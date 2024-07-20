package com.github.mapimage.data._2024.ukparliament;

import com.github.mapimage.data.ElectionDataLoader;
import com.github.mapimage.data.source.ukparliament.UkParliamentElectionData;
import com.github.mapimage.domain.Candidates;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.github.mapimage.data.ElectionYearDataSource.*;

@Slf4j
public class _2024UkParliamentElectionData implements ElectionDataLoader {

	/** using result 'by candidates' file, rather than 'by constituency', because the latter doesn't detail the smaller political parties */
	private static final String INPUT_DATA_FILE_RESULTS_BY_CANDIDATE = "HoC-GE2024-results-by-candidate.xlsx";
	private static final String INPUT_DATA_FILE_RESULTS_BY_CONSTITUENCY = "HoC-GE2024-results-by-constituency.xlsx";

	public Map<String, Candidates> apply(final Map<String, String> partyColorMapping) {
		return UkParliamentElectionData.loadElectionData(
				_2024UkParliament,
				INPUT_DATA_FILE_RESULTS_BY_CANDIDATE,
				INPUT_DATA_FILE_RESULTS_BY_CONSTITUENCY,
				partyColorMapping,
				2,
				7,
				17,
				12);
	}
}