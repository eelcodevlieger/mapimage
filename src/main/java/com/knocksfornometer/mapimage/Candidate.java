package com.knocksfornometer.mapimage;

import java.awt.Color;
import java.util.List;
import java.util.Map;

public class Candidate{

	private static final Color PARTY_COLOR_OTHER = Color.WHITE;
	
	private final Color partyColor;
	private final int percentage;
	
	public Candidate(Color color, int percentage) {
		this.partyColor = color;
		this.percentage = percentage;
	}

	public Candidate(Map<String, String> partyColorMapping, String partyCode, int percentage) {
		this(getPartyColor(partyCode, partyColorMapping), percentage);
	}

	/**
	 * @return Party Color as found in the {@value #CONSTITUENCY_NAME_MAPPING_FILE} config file
	 *         or null if mapping has not been defined
	 */
	private static Color getPartyColor(String partyCode, Map<String, String> partyColorMapping) {
		String colorCode = partyColorMapping.get( partyCode.toUpperCase() );
		Color partyColor;
		if(colorCode == null){
			System.out.println("Party unmapped [partyCode=" + partyCode + "]");
			partyColor = PARTY_COLOR_OTHER;
		}else{
			partyColor = Color.decode(colorCode);
		}
		return partyColor;
	}

	public Color getColor() {
		return partyColor;
	}

	public int getPercentage() {
		return percentage;
	}

	public static Candidate createNoVoteCandidate(List<Candidate> candidates) {
		int totalPercentage = 0;
		for(Candidate candidate : candidates)
			if(candidate != null)
				totalPercentage += candidate.getPercentage();
		
		return new Candidate(Color.BLACK, 100 - totalPercentage);
	}
	
	@Override
	public String toString() {
		return "Candidate [partyColor=" + partyColor + ", percentage=" + percentage + "]";
	}
}