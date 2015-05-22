package com.knocksfornometer.mapimage.data._2015.guardian;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.annotations.SerializedName;
import com.knocksfornometer.mapimage.Candidate;
import com.knocksfornometer.mapimage.Candidates;
import com.knocksfornometer.mapimage.json.JsonUtils;

public class _2015GuardianElectionData {
	private static final Color PARTY_COLOR_OTHER = Color.WHITE;

	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

	private static enum JsonDataLoadMethod { URL, FILE }
	private static final JsonDataLoadMethod JSON_INPUT_DATA_LOAD_METHOD = JsonDataLoadMethod.URL;
	
	// TODO move to properties file
	private static final String JSON_INPUT_DATA_URL = "http://visuals.guim.co.uk/2015/05/election/data/liveresults.json";
	private static final String JSON_INPUT_DATA_FILE = "src\\main\\resources\\election_data\\2015\\guardian\\liveresults.json";

	private final Map<String, String> partyNameAbbreviationToFullNameCache = new HashMap<>();


	public class JsonDataCandidate {
		public String name;
		public int votes;
		public double percentage;
		public String party;
	}

	public class JsonDataYearData {
		public JsonDataCandidate[] candidates;
		public long turnout;
		public double percentageTurnout;
		public long majority;
		public double percentageMajority;
		public Date updated;
		public double swing;
		public String winningParty;
		public String sittingParty;
		public long electorate;
		public String status;
	}

	public class JsonDataConstituency {
		@SerializedName("2015")
		public JsonDataYearData _2015;
		public String name;
		public String ons_id;
	}

	public class JsonDataParty {
		public String abbreviation;
		public String name;
		public int seats;
		public int gains;
		public int losses;
		public long votes;
		public double percentageShare;
		public double percentageChange;
		public int forecastSeats;
	}

	public class JsonDataPasop {
		public int numberOfResults;
		public int totalNumberOfConstituencies;
		public int totalVotes;
		public JsonDataParty[] parties;
	}

	public JsonDataPasop PASOP;

	public JsonDataConstituency[] constituencies;
	

	/**
	 * Load election data from JSON data.
	 */
	public static Map<String, Candidates> loadElectionData(Map<String, String> partyColorMapping, ConstituencyMapping constituencyMapping) throws IOException, MalformedURLException, FileNotFoundException, UnsupportedEncodingException {
		Reader jsonReader;
		if(JSON_INPUT_DATA_LOAD_METHOD == JsonDataLoadMethod.URL){
			jsonReader = new InputStreamReader( new URL(JSON_INPUT_DATA_URL).openStream() );
		}else{
			jsonReader = new FileReader(JSON_INPUT_DATA_FILE);
		}
		_2015GuardianElectionData electionData = _2015GuardianElectionData.fromJson( jsonReader );
		return electionData.loadCandidateMap(partyColorMapping, constituencyMapping);
	}
	
	/**
	 * Loads the JSON data and closes the Reader.
	 */
	private static _2015GuardianElectionData fromJson(Reader reader) throws IOException{
		return JsonUtils.fromJson(reader, _2015GuardianElectionData.class, DATE_FORMAT);
	}

	/**
	 * Convert the election data in to a map of constituency name key ({@link #toKey(String)}) to candidate data. 
	 */
	public Map<String, Candidates> loadCandidateMap(Map<String, String> partyColorMapping, ConstituencyMapping constituencyMapping) throws UnsupportedEncodingException, FileNotFoundException {
		Map<String, Candidates> constituencyCandidates = new HashMap<>();
		for(JsonDataConstituency constituency : constituencies){
			Candidate[] candidates = createCandidates(constituency, constituency._2015.candidates, partyColorMapping);
			constituencyCandidates.put( constituencyMapping.toKey(constituency.name), new Candidates(candidates) );
		}
		
		return constituencyCandidates;
	}

	/**
	 * create Array of candidates describing their vote percentage (normalised to include no-vote) and party colors (no vote = BLACK).
	 * 1% is the smallest unit, rounding down. Percentages add up to 100%.
	 */
	private Candidate[] createCandidates(JsonDataConstituency constituency, JsonDataCandidate[] candidateData, Map<String, String> partyColorMapping) {
		Candidate[] candidates = new Candidate[candidateData.length + 1]; // reserve one extra slot to represent people that didn't vote
		int percentageTurnout = (int)constituency._2015.percentageTurnout;
		
		Set<String> unmappedPartyCodes = new TreeSet<>();
		for (int i = 0; i < candidateData.length; i++) {
			JsonDataCandidate candidate = candidateData[i];
			int percentage = (int)(candidate.percentage / 100d * percentageTurnout);
			String partyCode = candidate.party;
			Color partyColor = getPartyColor(partyCode, partyColorMapping);
			if(partyColor == null){
				unmappedPartyCodes.add(partyCode);
				partyColor = PARTY_COLOR_OTHER;
			}
			candidates[i] = new Candidate(partyColor, percentage);
		}
		
		for (String unmappedPartyCode : unmappedPartyCodes) {
			System.out.println("Party unmapped [partyName=" + getPartyName(unmappedPartyCode) + ", partyCode=" + unmappedPartyCode + "]");
		}
		
		Candidate noVote = Candidate.createNoVoteCandidate( Arrays.asList(candidates) );
		candidates[candidates.length-1] = noVote;
		
		return candidates;
	}

	/**
	 * @return Party Color as found in the {@value #CONSTITUENCY_NAME_MAPPING_FILE} config file
	 *         or null if mapping has not been defined
	 */
	private Color getPartyColor(String partyCode, Map<String, String> partyColorMapping) {
		String colorCode = partyColorMapping.get(partyCode.toUpperCase());
		return colorCode != null ? Color.decode(colorCode) : null;
	}
	
	private String getPartyName(String partyCode){
		if(partyNameAbbreviationToFullNameCache.isEmpty())
			for(JsonDataParty party : PASOP.parties)
				partyNameAbbreviationToFullNameCache.put(party.abbreviation.toUpperCase(), party.name);
		
		return partyNameAbbreviationToFullNameCache.getOrDefault(partyCode, "UNKNOWN");
	}
}