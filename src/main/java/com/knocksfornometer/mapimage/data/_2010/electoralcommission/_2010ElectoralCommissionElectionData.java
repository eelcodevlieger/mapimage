package com.knocksfornometer.mapimage.data._2010.electoralcommission;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.knocksfornometer.mapimage.Candidate;
import com.knocksfornometer.mapimage.Candidates;

/**
 * 
 * @author Eelco de Vlieger
 */
public class _2010ElectoralCommissionElectionData {

	private static final String INPUT_DATA_FILE = "src\\main\\resources\\election_data\\2010\\electoral_commission\\GE2010-constituency-results-website.xls";

	// TODO duplicate code
	private static final Color PARTY_COLOR_OTHER = Color.WHITE;
	
	public static Map<String, Candidates> loadElectionData(Map<String, String> partyColorMapping) throws Exception {

		Map<String, Candidates> electionDataMap = new HashMap<>();
		
		Workbook wb;
		try(InputStream inp = new FileInputStream(INPUT_DATA_FILE)){
			wb = WorkbookFactory.create(inp);
		}
		
	    Sheet sheet = wb.getSheetAt(0);
	    
	    sheet = wb.getSheetAt(0);
	    
	    boolean isFirst = true;
	    boolean isConstituencyRow = true;
	    boolean isTurnoutRow = true;
	    String constituencyName = "";
	    double turnout = 0.0d;
	    List<Candidate> candidates = new ArrayList<>();
	    for (Row row : sheet) {
	    	
	    	// ignore header
	    	if(isFirst){
	    		isFirst = false;
	    		continue;
	    	}
	    	
	    	if(isConstituencyRow){
	    		constituencyName = row.getCell(0).getRichStringCellValue().getString();
		    	if(constituencyName == null || constituencyName.isEmpty() || constituencyName.startsWith("2005")){
		    		continue; // skip blank rows
		    	}
		    	constituencyName = constituencyName.substring(0, constituencyName.indexOf('[') ).trim();
	    		isConstituencyRow = false;
	    		continue;
	    	}
	    	
	    	if(isTurnoutRow){
		    	turnout = row.getCell(1).getNumericCellValue();
	    		isTurnoutRow = false;
	    	}
	    	
	    	String partyCode = row.getCell(3).getRichStringCellValue().getString();
	    	if(partyCode == null || partyCode.trim().isEmpty()){

	    		System.out.println("constituency election result [constituencyName=" + constituencyName + ", turnout=" + turnout + ", candidates=" + candidates + "]");
	    		candidates.add( Candidate.createNoVoteCandidate(candidates) );
	    		electionDataMap.put(constituencyName, new Candidates( candidates.toArray( new Candidate[candidates.size()] ) ) );
	    		
	    		// reset flags for next iteration
	    		isConstituencyRow = true;
	    		isTurnoutRow = true;
	    		candidates.clear();
	    		
	    		continue;
	    	}
	    	
	    	// vote share
	    	double voteShare = row.getCell(5).getNumericCellValue();
	    	
	    	if(turnout < 0.1d)
	    		throw new IllegalStateException("Turnout percentage invalid [turnout=" + turnout + "]");
	    	
	    	Color partyColor = getPartyColor(partyCode, partyColorMapping);
			if(partyColor == null){
				// TODO party name
				System.out.println("Party unmapped [partyCode=" + partyCode + "]");
				partyColor = PARTY_COLOR_OTHER;
			}
			candidates.add( new Candidate(partyColor , (int)(voteShare / 100 * turnout) ));
	    }
	    
	    return electionDataMap;
	}


	private static Color getPartyColor(String partyCode, Map<String, String> partyColorMapping) {
		String colorCode = partyColorMapping.get(partyCode.toUpperCase());
		return colorCode != null ? Color.decode(colorCode) : null;
	}
}