package com.knocksfornometer.mapimage.data.source.ukparliament;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.knocksfornometer.mapimage.data.ElectionYearDataSource;
import com.knocksfornometer.mapimage.domain.Candidate;
import com.knocksfornometer.mapimage.domain.Candidates;
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

import static com.knocksfornometer.mapimage.Main.CONFIG;

@Slf4j
public class UkParliamentElectionData {

	@SneakyThrows
	public static Map<String, Candidates> loadElectionData(final ElectionYearDataSource electionYearDataSource,
														   final String resultsInputFileByCandidate,
														   final String resultsInputFileByConstituency,
														   final Map<String, String> partyColorMapping,
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
		return loadElectionData(resultsByCandidateWorkBook, partyColorMapping, electorateSizeData, headerRowIndex, partyIdentifierColumnIndex, numVotesColumnIndex);
	}

	/**
	 * @return Map of constituency name ->
	 */
	private static Map<String, Candidates> loadElectionData(final Workbook workBook,
															final Map<String, String> partyColorMapping,
															final Map<String, Long> electorateSizeData,
															final int headerRowIndex,
															final int partyIdentifierColumnIndex,
															final int numVotesColumnIndex) {
		final ListMultimap<String, Candidate> electionDataMap = ArrayListMultimap.create();

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
			final Candidate candidate = new Candidate(partyColorMapping, toPartyCode(partyIdentifier), percentageOfTotalElectorate);
			electionDataMap.put(constituencyName, candidate);
	    }
	    
	    return electionDataMap.asMap()
	    		.entrySet()
	    		.stream()
	    		.collect(Collectors.toMap(Entry::getKey,
	    				entry -> {
	    					final Collection<Candidate> candidates = entry.getValue();
	    					final Candidate candidateAndNotVoted = Candidate.createNoVoteCandidate(candidates);
	    					final Candidate[] candidatesAndNotVoted = candidates.toArray(new Candidate[candidates.size() + 1]);
	    					candidatesAndNotVoted[candidates.size()] = candidateAndNotVoted;
	    					return new Candidates(candidatesAndNotVoted);
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

    private static String toPartyCode(String partyIdentifier) {
		return switch (partyIdentifier) {
			case "Speaker" -> "SPK";
			case "Conservative" -> "CON";
			case "Labour" -> "LAB";
			case "Liberal Democrats" -> "LD";
			case "Green Party" -> "GREEN";
			case "Plaid Cymru" -> "PC";
			case "Sinn Féin" -> "SF";
			case "ED", "EDP" -> "ENG DEM";
			case "Independent", "Ashfield" -> "IND"; // Ashfield Independents Putting People Before Politics
			case "PBP Alliance" -> "PBPA";
			case "AGS" -> "GREEN SOC";
			case "NHAP" -> "NATIONAL HEALTH ACTION PARTY";
			default -> partyIdentifier;
		};
    }

	private static String toKey(String constituencyName) {
        return constituencyName.toLowerCase();
    }
}