package com.github.mapimage.data._2015.electoralcommission;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.github.mapimage.data.ElectionDataLoader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ListMultimap;
import com.github.mapimage.domain.Candidate;
import com.github.mapimage.domain.Candidates;

import static com.github.mapimage.Main.CONFIG;
import static com.github.mapimage.data.ElectionYearDataSource._2015ElectoralCommission;

@Slf4j
public class _2015ElectoralCommissionElectionData implements ElectionDataLoader {

	private static final String INPUT_DATA_FILE = "2015-UK-general-election-data-results-WEB.xlsx";
	
	@SneakyThrows
	public Map<String, Candidates> apply(final Map<String, String> partyColorMapping){
		final Workbook workBook;
		try(InputStream inputStream = new FileInputStream(CONFIG.getElectionYearDataSourceResourcePath(_2015ElectoralCommission) + INPUT_DATA_FILE)){
			workBook = WorkbookFactory.create(inputStream);
		}
		
		final Map<String, Double> turnoutData = loadTurnoutData(workBook);
		return loadElectionData(workBook, partyColorMapping, turnoutData);
	}

	private static Map<String, Double> loadTurnoutData(final Workbook workBook) {
		final Map<String, Double> turnoutData = new HashMap<>();
		final Sheet resultSheet = workBook.getSheet("Results for analysis");
	    boolean isHeaderRow = true;
		for(Row row : resultSheet) {
	    	
			// ignore header
	    	if(isHeaderRow){
	    		isHeaderRow = false;
	    		continue;
	    	}

	    	if(row.getCell(0) == null) {
	    		break; // end of rows
	    	}
	    	
			final String constituencyName = row.getCell(1).getStringCellValue();
			final double totalElectorate = row.getCell(7).getNumericCellValue();
			final double totalVotes = row.getCell(8).getNumericCellValue();
			turnoutData.put(constituencyName, totalVotes / totalElectorate * 100d);
		}
		return turnoutData;
	}

	private static Map<String, Candidates> loadElectionData(final Workbook workBook, final Map<String, String> partyColorMapping, final Map<String, Double> turnoutData) {

		final ListMultimap<String, Candidate> electionDataMap = ArrayListMultimap.create();

	    final Sheet candidatesSheet = workBook.getSheet("Candidates");
	    
	    boolean isHeaderRow = true;
	    
	    for (Row row : candidatesSheet) {
	    	
	    	// ignore header
	    	if(isHeaderRow){
	    		isHeaderRow = false;
	    		continue;
	    	}
	    	
	    	if(row.getCell(0) == null) {
	    		break; // end of rows
	    	}
	    	
	    	final String constituencyName = row.getCell(3).getStringCellValue();
	    	final double voteShare = row.getCell(6).getNumericCellValue();
	    	final String partyCode = row.getCell(17).getStringCellValue();
			final Double turnout = getTurnoutPercentage(turnoutData, constituencyName);
			if(turnout == null) {
                log.error("No turnout data found [constituencyName={}]", constituencyName);
				continue;
			}
			electionDataMap.put(constituencyName, new Candidate(partyColorMapping, partyCode, (int)Math.round( voteShare / 100 * turnout )));
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

	private static Double getTurnoutPercentage(final Map<String, Double> turnoutData, final String constituencyName) {
		String key = constituencyName.replace(" and ", " & ");
		Double result = turnoutData.get(key);
		if(result == null) {
			result = turnoutData.get(key.replace('-', ' ').replace(",", ""));
		}
		
		// source data is messy. There are a lot of reversals like 'Central Devon' <-> 'Devon Central'
		if(result == null) {
			final Collection<List<String>> keyPermutations = Collections2.permutations( Arrays.asList(key.split(" ")) );
			for (List<String> keyPermutation : keyPermutations) {
				result = turnoutData.get( Joiner.on(" ").join(keyPermutation) );
				if(result != null) {
					break;
				}
			}
		}
		
		if(result == null && key.startsWith("City Of ")) {
			result = turnoutData.get(key.substring(8) + ", City Of");
		}

		if(result == null && key.startsWith("The ")) {
			result = turnoutData.get(key.substring(4) + ", The");
		}
		
		if(result == null && key.startsWith("Kingston upon ")) {
			result = turnoutData.get(key.substring(14));
		}
		
		if(result == null && key.equals("Na h-Eileanan An Iar")) {
			result = turnoutData.get("Na H-Eileanan An Iar");
		}
				
		return result;
	}
}