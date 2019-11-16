package com.knocksfornometer.mapimage.data._2017.electoral_commission;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.knocksfornometer.mapimage.Candidate;
import com.knocksfornometer.mapimage.Candidates;

/**
 * 
 * @author Eelco de Vlieger
 */
public class _2017ElectoralCommissionElectionData {

	private static final String INPUT_DATA_FILE = "src\\main\\resources\\election_data\\2017\\electoral_commission\\2017-UKPGE-Electoral-Data.xls";
	
	public static Map<String, Candidates> loadElectionData(Map<String, String> partyColorMapping) throws Exception {
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

	private static Map<String, Candidates> loadElectionData(final Workbook workBook, final Map<String, String> partyColorMapping, final Map<String, Long> electorateSizeData) throws Exception {

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
			electionDataMap.put(clean(constituencyName), new Candidate(partyColorMapping, toPartyCode(partyIdentifier), (int)Math.round( 100.0 / (electorateSize / (double)numVotes) )));
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

    private static String toPartyCode(String partyIdentifier) {
        switch (partyIdentifier) {
            case "Speaker":
                return "SPK";
            case "Conservative":
                return "CON";
            case "Labour":
                return "LAB";
            case "Liberal Democrats":
                return "LD";
            case "Green Party":
                return "GREEN";
            case "Plaid Cymru":
                return "PC";
            case "Sinn Féin":
                return "SF";
            case "ED":
            case "EDP":
                return "ENG DEM";
            case "Independent":
            case "Ashfield": // Ashfield Independents Putting People Before Politics
                return "IND";
            case "PBP Alliance":
                return "PBPA";
            case "AGS":
                return "GREEN SOC";
            case "NHAP":
                return "NATIONAL HEALTH ACTION PARTY";
            default:
                return partyIdentifier;
        }
    }

    private static @Nullable String clean(String constituencyName) {
        return constituencyName.replaceFirst("\\s?\\d$", ""); // remove trailing digit on end of constituency name
    }

	private static String toKey(String constituencyName) {
        return clean(constituencyName).toLowerCase();
    }
}