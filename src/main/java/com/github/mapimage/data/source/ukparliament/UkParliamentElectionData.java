package com.github.mapimage.data.source.ukparliament;

import com.github.mapimage.domain.CandidateResult;
import com.github.mapimage.domain.Party;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.github.mapimage.data.ElectionYearDataSource;
import com.github.mapimage.domain.CandidateResults;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.github.mapimage.Main.CONFIG;

@Slf4j
public class UkParliamentElectionData {

	@SneakyThrows
	public static Map<String, CandidateResults> loadElectionData(final ElectionYearDataSource electionYearDataSource,
                                                                 final String resultsInputFileByCandidate,
                                                                 final String resultsInputFileByConstituency,
                                                                 final int headerRowIndex,
                                                                 final int partyIdentifierColumnIndex,
                                                                 final int numVotesColumnIndex,
                                                                 final int totalElectorateColumnIndex) {
		final Workbook resultsByCandidateWorkBook;
		try(InputStream inputStream = new FileInputStream(CONFIG.getElectionYearDataSourceResourcePath(electionYearDataSource) + resultsInputFileByCandidate)){
			resultsByCandidateWorkBook = WorkbookFactory.create(inputStream);
		}
		final Workbook resultsByConstituencyWorkBook;
		try(InputStream inputStream = new FileInputStream(CONFIG.getElectionYearDataSourceResourcePath(electionYearDataSource) + resultsInputFileByConstituency)){
			resultsByConstituencyWorkBook = WorkbookFactory.create(inputStream);
		}
		final Map<String, Long> electorateSizeData = loadElectorateSizeData(resultsByConstituencyWorkBook, headerRowIndex, totalElectorateColumnIndex);
		return loadElectionData(resultsByCandidateWorkBook, electorateSizeData, headerRowIndex, partyIdentifierColumnIndex, numVotesColumnIndex);
	}

	/**
	 * @return Map of constituency name ->
	 */
	private static Map<String, CandidateResults> loadElectionData(final Workbook workBook,
                                                                  final Map<String, Long> electorateSizeData,
                                                                  final int headerRowIndex,
                                                                  final int partyIdentifierColumnIndex,
                                                                  final int numVotesColumnIndex) {
		final ListMultimap<String, CandidateResult> electionDataMap = ArrayListMultimap.create();

	    final Sheet candidatesSheet = workBook.getSheetAt(0);
	    
	    for (Row row : candidatesSheet) {
            if(row.getRowNum() <= headerRowIndex){
                continue; // ignore header
            }
	    	
	    	if(row.getCell(0) == null) {
	    		break; // end of rows
	    	}
	    	
	    	final String constituencyName = row.getCell(2).getStringCellValue();
//			final String partyName = row.getCell(7).getStringCellValue();
			final String partyIdentifier = row.getCell(partyIdentifierColumnIndex).getStringCellValue();
			final double numVotes = row.getCell(numVotesColumnIndex).getNumericCellValue();
//			final double voteShare = row.getCell(15).getNumericCellValue();
//			int percentage = (int) Math.round(100.0 * voteShare);

			final Long electorateSize = electorateSizeData.get( toKey(constituencyName) );
			if(electorateSize == null) {
                log.error("No electorate size data found [constituencyName={}]", constituencyName);
				continue;
			}
			final int percentageOfTotalElectorate = (int) Math.round(100.0 / (electorateSize / numVotes));
			final CandidateResult candidateResult = new CandidateResult(Party.getByAbbreviation(partyIdentifier, percentageOfTotalElectorate), percentageOfTotalElectorate);
			electionDataMap.put(constituencyName, candidateResult);
	    }
	    
	    return electionDataMap.asMap()
	    		.entrySet()
	    		.stream()
	    		.collect(Collectors.toMap(Entry::getKey,
	    				entry -> {
	    					final Collection<CandidateResult> candidateResults = entry.getValue();
	    					final CandidateResult candidateResultAndNotVoted = CandidateResult.createNoVoteCandidate(candidateResults);
	    					final CandidateResult[] candidatesAndNotVoted = candidateResults.toArray(new CandidateResult[candidateResults.size() + 1]);
	    					candidatesAndNotVoted[candidateResults.size()] = candidateResultAndNotVoted;
	    					return new CandidateResults(candidatesAndNotVoted);
	    				}
	    			)
	    		);
	}

	private static Map<String, Long> loadElectorateSizeData(final Workbook workBook, final int headerIndex, int totalElectorateColumnIndex) {
		final Map<String, Long> electorateSizeData = new HashMap<>();
		final Sheet constituencySheet = workBook.getSheetAt(0);

		for(Row row : constituencySheet) {
			if(row.getRowNum() <= headerIndex){
				continue; // ignore header
			}

			if(row.getCell(0) == null) {
				break; // end of rows
			}

			final String constituencyName = row.getCell(2).getStringCellValue();
			final Long totalElectorate = Math.round(row.getCell(totalElectorateColumnIndex).getNumericCellValue());
			electorateSizeData.put(toKey(constituencyName), totalElectorate);
		}
		return electorateSizeData;
	}

	private static String toKey(String constituencyName) {
        return constituencyName.toLowerCase();
    }
}