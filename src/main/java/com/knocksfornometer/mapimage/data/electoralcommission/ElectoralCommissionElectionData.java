package com.knocksfornometer.mapimage.data.electoralcommission;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.knocksfornometer.mapimage.Candidate;
import com.knocksfornometer.mapimage.Candidates;

/**
 * Process Excel data from the Electoral Commission
 * http://www.electoralcommission.org.uk/our-work/our-research/electoral-data
 * 
 * @author Eelco de Vlieger
 */
public class ElectoralCommissionElectionData {

	public static Map<String, Candidates> loadElectionData(String inputDataFile, Map<String, String> partyColorMapping, boolean blankResultsOnConstituencyRow, int numInitialRowsToSkip) throws Exception {

		Map<String, Candidates> electionDataMap = new HashMap<>();
		
		Workbook wb;
		try(InputStream inp = new FileInputStream(inputDataFile)){
			wb = WorkbookFactory.create(inp);
		}
		
	    Sheet sheet = wb.getSheetAt(0);
	    
	    sheet = wb.getSheetAt(0);
	    
	    boolean isConstituencyRow = true;
	    boolean isTurnoutRow = true;
	    String constituencyName = "";
	    double turnout = 0.0d;
	    List<Candidate> candidates = new ArrayList<>();
	    for (Row row : sheet) {
	    	
	    	// ignore header
	    	if(numInitialRowsToSkip > 0){
	    		numInitialRowsToSkip--;
	    		continue;
	    	}
	    	
	    	if(isConstituencyRow){
	    		Cell cell = row.getCell(0);
	    		if(cell == null)
	    			continue;
	    		
				constituencyName = cell.getRichStringCellValue().getString();
		    	if(constituencyName == null || constituencyName.isEmpty() || constituencyName.startsWith("200"))
		    		continue; // skip blank rows
		    	
		    	constituencyName = constituencyName.substring(0, constituencyName.indexOf('[') ).trim();
	    		isConstituencyRow = false;
	    		
	    		if(blankResultsOnConstituencyRow)
	    			continue;
	    	}
	    	
	    	if(isTurnoutRow){
		    	Cell turnoutCell = row.getCell(1);
		    	if(turnoutCell == null)
		    		continue;
		    	
				turnout = turnoutCell.getNumericCellValue();
	    		isTurnoutRow = false;
	    	}
	    	
	    	Cell partyCodeCell = row.getCell(3);
	    	if(partyCodeCell == null)
	    		continue;
	    	
			String partyCode = partyCodeCell.getRichStringCellValue().getString();
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
	    	
			candidates.add( new Candidate(partyColorMapping, partyCode, (int)(voteShare / 100 * turnout) ) );
	    }
	    
	    return electionDataMap;
	}
}