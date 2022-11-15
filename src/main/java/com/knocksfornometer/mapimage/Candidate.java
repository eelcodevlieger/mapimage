package com.knocksfornometer.mapimage;

import java.awt.Color;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

public class Candidate{

	private static final Color PARTY_COLOR_OTHER = Color.WHITE;
	private static final Color PARTY_COLOR_NO_VOTE = Color.BLACK;

	private final Color partyColor;
	private final int percentageOfTotalElectorate;

	public Candidate(final Color color, final int percentageOfTotalElectorate) {
		this.partyColor = color;
		this.percentageOfTotalElectorate = percentageOfTotalElectorate;
		Preconditions.checkArgument(percentageOfTotalElectorate >= 0 && percentageOfTotalElectorate <= 100, "percentageOfTotalElectorate should be in range 0 to 100 [percentageOfTotalElectorate=" + percentageOfTotalElectorate + "]");
	}

	public Candidate(final Map<String, String> partyColorMapping, final String partyCode, final int percentage) {
		this(getPartyColor(partyCode, partyColorMapping, percentage), percentage);
	}

	/**
	 * @return Party Color as found in the {@value #CONSTITUENCY_NAME_MAPPING_FILE} config file
	 *         or null if mapping has not been defined
	 */
	private static Color getPartyColor(final String partyCode, final Map<String, String> partyColorMapping, final int percentage) {
		final String colorCode = partyColorMapping.get( partyCode.toUpperCase() );
		final Color partyColor;
		if(colorCode == null) {
			(percentage > 5 ? System.err : System.out).println("Party unmapped [partyCode=" + partyCode + "]");
			partyColor = PARTY_COLOR_OTHER;
		} else {
			partyColor = Color.decode(colorCode);
		}
		return partyColor;
	}

	public Color getColor() {
		return partyColor;
	}

	public int getPercentageOfTotalElectorate() {
		return percentageOfTotalElectorate;
	}

	/**
	 * @param candidates - All candidates (and their vote as percentage of the total electorate) of a constituency
	 * @return Create a Candidate to represent the 'no vote' share of the population.
	 */
	public static Candidate createNoVoteCandidate(final Collection<Candidate> candidates) {
		final int totalPercentage = candidates.stream().map(Candidate::getPercentageOfTotalElectorate).mapToInt(Integer::intValue).sum();
		return new Candidate(PARTY_COLOR_NO_VOTE, 100 - totalPercentage);
	}
	
	@Override
	public String toString() {
		return "Candidate [partyColor=" + partyColor + ", percentageOfTotalElectorate=" + percentageOfTotalElectorate + "]";
	}
}