package com.github.mapimage.data._2017.electoralcommission;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.github.mapimage.data.ElectionDataLoader;
import com.github.mapimage.domain.CandidateResult;
import com.github.mapimage.domain.Party;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.github.mapimage.domain.CandidateResults;

import static com.github.mapimage.Main.CONFIG;
import static com.github.mapimage.data.ElectionYearDataSource._2017ElectoralCommission;

@Slf4j
public class _2017ElectoralCommissionElectionData implements ElectionDataLoader {

	private static final String INPUT_DATA_FILE = "2017-UKPGE-Electoral-Data.xls";
	
	@SneakyThrows
	public Map<String, CandidateResults> load() {
		final Workbook workBook;
		try(InputStream inputStream = new FileInputStream(CONFIG.getElectionYearDataSourceResourcePath(_2017ElectoralCommission) + INPUT_DATA_FILE)){
			workBook = WorkbookFactory.create(inputStream);
		}
		
		final Map<String, Long> electorateSizeData = loadElectorateSizeData(workBook);
		return loadElectionData(workBook, electorateSizeData);
	}

	private static Map<String, Long> loadElectorateSizeData(final Workbook workBook) {
		final Map<String, Long> electorateSizeData = new HashMap<>();
		final Sheet resultSheet = workBook.getSheet("Administrative data");
		for(Row row : resultSheet) {
	    	if(row.getRowNum() <= 2){
	    		continue; // ignore header
	    	}

	    	if(row.getCell(0) == null) {
	    		break; // end of rows
	    	}
	    	
			final String constituencyName = row.getCell(2).getStringCellValue();
			final Long totalElectorate = Math.round(row.getCell(3).getNumericCellValue());
			electorateSizeData.put(toKey(constituencyName), totalElectorate);
		}
		return electorateSizeData;
	}

	private static Map<String, CandidateResults> loadElectionData(final Workbook workBook, final Map<String, Long> electorateSizeData) {

		final ListMultimap<String, CandidateResult> electionDataMap = ArrayListMultimap.create();

	    final Sheet candidatesSheet = workBook.getSheet("Results");
	    
	    for (Row row : candidatesSheet) {
            if(row.getRowNum() <= 1){
                continue; // ignore header
            }
	    	
	    	if(row.getCell(0) == null) {
	    		break; // end of rows
	    	}
	    	
	    	final String constituencyName = row.getCell(2).getStringCellValue();
	    	final String partyIdentifier = row.getCell(6).getStringCellValue(); 
	    	final long numVotes = Math.round(row.getCell(7).getNumericCellValue());
			final Long electorateSize = electorateSizeData.get( toKey(constituencyName) );
			if(electorateSize == null) {
                log.error("No electorate size data found [constituencyName={}]", constituencyName);
				continue;
			}
			final int percentageOfTotalElectorate = (int) Math.round(100.0 / (electorateSize / (double) numVotes));
			final CandidateResult candidateResult = new CandidateResult(Party.getByAbbreviation(partyIdentifier, percentageOfTotalElectorate), percentageOfTotalElectorate);
			electionDataMap.put(clean(constituencyName), candidateResult);
	    }
	    
	    return electionDataMap.asMap()
	    		.entrySet()
	    		.stream()
	    		.collect(Collectors.toMap(Entry::getKey,
	    				entry -> {
	    					final Collection<CandidateResult> candidateResults = entry.getValue();
	    					final CandidateResult noVoteCandidateResult = CandidateResult.createNoVoteCandidate(candidateResults);
	    					final CandidateResult[] candidatesAndNotVoted = candidateResults.toArray(new CandidateResult[candidateResults.size() + 1]);
	    					candidatesAndNotVoted[candidateResults.size()] = noVoteCandidateResult;
	    					return new CandidateResults(candidatesAndNotVoted);
	    				}
	    			)
	    		);
	}

    private static String clean(String constituencyName) {
        return constituencyName.replaceFirst("\\s?\\d$", ""); // remove trailing digit on end of constituency name
    }

	private static String toKey(String constituencyName) {
        return clean(constituencyName).toLowerCase();
    }
}