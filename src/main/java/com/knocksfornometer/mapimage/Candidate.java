package com.knocksfornometer.mapimage;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;

import com.google.common.base.Preconditions;

public class Candidate{

	private static final Color PARTY_COLOR_OTHER = Color.WHITE;
	
	private final Color partyColor;
	private final int percentage;
	
	public Candidate(Color color, int percentage) {
		this.partyColor = color;
		this.percentage = percentage;
		Preconditions.checkArgument(percentage >= 0 && percentage <= 100, "percentage should be in range 0 to 100");
	}

	public Candidate(Map<String, String> partyColorMapping, String partyCode, int percentage) {
		this(getPartyColor(partyCode, partyColorMapping, percentage), percentage);
	}

	/**
	 * @return Party Color as found in the {@value #CONSTITUENCY_NAME_MAPPING_FILE} config file
	 *         or null if mapping has not been defined
	 */
	private static Color getPartyColor(final String partyCode, Map<String, String> partyColorMapping, final int percentage) {
		String colorCode = partyColorMapping.get( partyCode.toUpperCase() );
		Color partyColor;
		if(colorCode == null){
			(percentage > 5 ? System.err : System.out).println("Party unmapped [partyCode=" + partyCode + "]");
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

	public static Candidate createNoVoteCandidate(Collection<Candidate> candidates) {
		int totalPercentage = 0;
		for(Candidate candidate : candidates) {
			if(candidate != null) {
				totalPercentage += candidate.getPercentage();
			}
		}
		
		return new Candidate(Color.BLACK, 100 - totalPercentage);
	}
	
	@Override
	public String toString() {
		return "Candidate [partyColor=" + partyColor + ", percentage=" + percentage + "]";
	}
}