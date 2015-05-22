package com.knocksfornometer.mapimage;

import java.awt.Color;
import java.util.List;

public class Candidate{

	private final Color partyColor;
	private final int percentage;
	
	public Candidate(Color color, int percentage) {
		this.partyColor = color;
		this.percentage = percentage;
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