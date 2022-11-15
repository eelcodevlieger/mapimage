package com.knocksfornometer.mapimage.data._2019.ukparliament;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.knocksfornometer.mapimage.Candidate;
import com.knocksfornometer.mapimage.Candidates;
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

/**
 * 
 * @author Eelco de Vlieger
 */
public class _2019UkParliamentElectionData {

	/** using result 'by candidates' file, rather than 'by constituency', because the latter doesn't detail the smaller political parties */
	private static final String INPUT_DATA_FILE_RESULTS_BY_CANDIDATE = "src\\main\\resources\\election_data\\2019\\uk_parliament\\HoC-GE2019-results-by-candidate-xlsx.xlsx";
	private static final String INPUT_DATA_FILE_RESULTS_BY_CONSTITUENCY = "src\\main\\resources\\election_data\\2019\\uk_parliament\\HoC-GE2019-results-by-constituency-xlsx.xlsx";

	public static Map<String, Candidates> loadElectionData(final Map<String, String> partyColorMapping) throws Exception {
		final Workbook resultsByCandidateWorkBook;
		try(InputStream inputStream = new FileInputStream(INPUT_DATA_FILE_RESULTS_BY_CANDIDATE)){
			resultsByCandidateWorkBook = WorkbookFactory.create(inputStream);
		}
		final Workbook resultsByConstituencyWorkBook;
		try(InputStream inputStream = new FileInputStream(INPUT_DATA_FILE_RESULTS_BY_CONSTITUENCY)){
			resultsByConstituencyWorkBook = WorkbookFactory.create(inputStream);
		}
		final Map<String, Long> electorateSizeData = loadElectorateSizeData(resultsByConstituencyWorkBook);
		return loadElectionData(resultsByCandidateWorkBook, partyColorMapping, electorateSizeData);
	}


	/**
	 * @param workBook
	 * @param partyColorMapping
	 * @param electorateSizeData
	 * @return Map of constituency name ->
	 */
	private static Map<String, Candidates> loadElectionData(final Workbook workBook, final Map<String, String> partyColorMapping, Map<String, Long> electorateSizeData) {
		final ListMultimap<String, Candidate> electionDataMap = ArrayListMultimap.create();

	    final Sheet candidatesSheet = workBook.getSheetAt(0);
	    
	    for (Row row : candidatesSheet) {
            if(row.getRowNum() <= 0){
                continue; // ignore header
            }
	    	
	    	if(row.getCell(0) == null) {
	    		break; // end of rows
	    	}
	    	
	    	final String constituencyName = row.getCell(2).getStringCellValue();
//			final String partyName = row.getCell(7).getStringCellValue();
			final String partyIdentifier = row.getCell(8).getStringCellValue();
			final double numVotes = row.getCell(14).getNumericCellValue();
//			final double voteShare = row.getCell(15).getNumericCellValue();
//			int percentage = (int) Math.round(100.0 * voteShare);

			final Long electorateSize = electorateSizeData.get( toKey(constituencyName) );
			if(electorateSize == null) {
				System.err.println("No electorate size data found [constituencyName=" + constituencyName + "]");
				continue;
			}
			final int percentageOfTotalElectorate = (int) Math.round(100.0 / (electorateSize / numVotes));
			final Candidate candidate = new Candidate(partyColorMapping, toPartyCode(partyIdentifier), percentageOfTotalElectorate);
			electionDataMap.put(clean(constituencyName), candidate);
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

	private static Map<String, Long> loadElectorateSizeData(final Workbook workBook) {
		final Map<String, Long> electorateSizeData = new HashMap<>();
		final Sheet constituencySheet = workBook.getSheetAt(0);

		for(Row row : constituencySheet) {
			if(row.getRowNum() <= 0){
				continue; // ignore header
			}

			if(row.getCell(0) == null) {
				break; // end of rows
			}

			final String constituencyName = row.getCell(2).getStringCellValue();
			final Long totalElectorate = Math.round(row.getCell(14).getNumericCellValue());
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

    private static String clean(String constituencyName) {
        return constituencyName;//constituencyName.replaceFirst("\\s?\\d$", ""); // remove trailing digit on end of constituency name
    }

	private static String toKey(String constituencyName) {
        return clean(constituencyName).toLowerCase();
    }
}