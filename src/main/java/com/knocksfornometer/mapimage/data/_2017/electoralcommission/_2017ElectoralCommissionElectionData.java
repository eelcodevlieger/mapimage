package com.knocksfornometer.mapimage.data._2017.electoralcommission;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.knocksfornometer.mapimage.data.ElectionDataLoader;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.knocksfornometer.mapimage.domain.Candidate;
import com.knocksfornometer.mapimage.domain.Candidates;

/**
 * 
 * @author Eelco de Vlieger
 */
public class _2017ElectoralCommissionElectionData implements ElectionDataLoader {

	private static final String INPUT_DATA_FILE = "src\\main\\resources\\election_data\\2017\\electoral_commission\\2017-UKPGE-Electoral-Data.xls";
	
	@SneakyThrows
	public Map<String, Candidates> apply(Map<String, String> partyColorMapping) {
		final Workbook workBook;
		try(InputStream inputStream = new FileInputStream(INPUT_DATA_FILE)){
			workBook = WorkbookFactory.create(inputStream);
		}
		
		final Map<String, Long> electorateSizeData = loadElectorateSizeData(workBook);
		return loadElectionData(workBook, partyColorMapping, electorateSizeData);
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

	private static Map<String, Candidates> loadElectionData(final Workbook workBook, final Map<String, String> partyColorMapping, final Map<String, Long> electorateSizeData) {

		final ListMultimap<String, Candidate> electionDataMap = ArrayListMultimap.create();

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
				System.err.println("No electorate size data found [constituencyName=" + constituencyName + "]");
				continue;
			}
			final int percentageOfTotalElectorate = (int) Math.round(100.0 / (electorateSize / (double) numVotes));
			final Candidate candidate = new Candidate(partyColorMapping, toPartyCode(partyIdentifier), percentageOfTotalElectorate);
			electionDataMap.put(clean(constituencyName), candidate);
	    }
	    
	    return electionDataMap.asMap()
	    		.entrySet()
	    		.stream()
	    		.collect(Collectors.toMap(Entry::getKey,
	    				entry -> {
	    					final Collection<Candidate> candidates = entry.getValue();
	    					final Candidate noVoteCandidate = Candidate.createNoVoteCandidate(candidates);
	    					final Candidate[] candidatesAndNotVoted = candidates.toArray(new Candidate[candidates.size() + 1]);
	    					candidatesAndNotVoted[candidates.size()] = noVoteCandidate;
	    					return new Candidates(candidatesAndNotVoted);
	    				}
	    			)
	    		);
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
        return constituencyName.replaceFirst("\\s?\\d$", ""); // remove trailing digit on end of constituency name
    }

	private static String toKey(String constituencyName) {
        return clean(constituencyName).toLowerCase();
    }
}