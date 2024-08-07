package com.github.mapimage.data.source.electoralcommission;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mapimage.data.ElectionYearDataSource;
import com.github.mapimage.domain.CandidateResult;
import com.github.mapimage.domain.Party;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.github.mapimage.domain.CandidateResults;

import static com.github.mapimage.Main.CONFIG;

/**
 * Process Excel data from the Electoral Commission
 * http://www.electoralcommission.org.uk/our-work/our-research/electoral-data
 */
@Slf4j
public class ElectoralCommissionElectionData {

	public static Map<String, CandidateResults> loadElectionData(final ElectionYearDataSource electionYearDataSource,
																 final String inputDataFile,
																 final boolean blankResultsOnConstituencyRow,
																 int numInitialRowsToSkip) throws Exception {
	    Map<String, CandidateResults> electionDataMap = new HashMap<>();

        Workbook workBook;
        try(InputStream inputStream = new FileInputStream(CONFIG.getElectionYearDataSourceResourcePath(electionYearDataSource) + inputDataFile)){
            workBook = WorkbookFactory.create(inputStream);
        }
        Sheet sheet = workBook.getSheetAt(0);
	    
	    boolean isConstituencyRow = true;
	    boolean isTurnoutRow = true;
	    String constituencyName = "";
	    double turnout = 0.0d;
	    List<CandidateResult> candidateResults = new ArrayList<>();
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
                log.debug("constituency election result [constituencyName={}, turnout={}, candidates={}]", constituencyName, turnout, candidateResults);
	    		candidateResults.add( CandidateResult.createNoVoteCandidate(candidateResults) );
	    		electionDataMap.put(constituencyName, new CandidateResults( candidateResults.toArray( new CandidateResult[candidateResults.size()] ) ) );
	    		
	    		// reset flags for next iteration
	    		isConstituencyRow = true;
	    		isTurnoutRow = true;
	    		candidateResults.clear();
	    		
	    		continue;
	    	}
	    	
	    	// vote share
	    	double voteShare = row.getCell(5).getNumericCellValue();
	    	
	    	if(turnout < 0.1d) {
				throw new IllegalStateException("Turnout percentage invalid [turnout=" + turnout + "]");
			}

			final int percentage = (int) (voteShare / 100 * turnout);
			candidateResults.add( new CandidateResult(Party.getByAbbreviation(partyCode, percentage), percentage) );
	    }
	    
	    return electionDataMap;
	}
}