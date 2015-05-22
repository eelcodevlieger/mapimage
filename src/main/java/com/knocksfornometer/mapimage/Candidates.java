package com.knocksfornometer.mapimage;

import java.util.Arrays;
import java.util.Random;

public class Candidates{

	private final Candidate[] candidates;
	/** Candidates representation percentage as array of size 100 (filled with {@link Candidate} references by that ratio) */
	private final Candidate[] candidatesExpanded;
    private final Random random = new Random();
	
	public Candidates(Candidate[] candidates) {
		this.candidates = validate(candidates);
		candidatesExpanded = expand(candidates);
	}

	private Candidate[] expand(Candidate[] candidates) {
		final Candidate[] candidatesExpanded = new Candidate[100];
		int index = 0;
		for (Candidate candidate : candidates)
			for (int i = 0; i < candidate.getPercentage(); i++)
				candidatesExpanded[index++] = candidate;
		return candidatesExpanded;
	}

	private Candidate[] validate(Candidate[] candidates) {
		int totalPercentage = 0;
		for (Candidate candidate : candidates)
			totalPercentage += candidate.getPercentage();
		if(totalPercentage != 100)
			throw new IllegalStateException("Candidates percentage should sum to 100");
		return candidates;
	}

	public Candidate[] getCandidates() {
		return candidates;
	}

	public Candidate[] getCandidatesExpanded() {
		return candidatesExpanded;
	}

	public Candidate getNextCandidate() {
		return candidatesExpanded[ random.nextInt(candidatesExpanded.length) ];
	}
	
	@Override
	public String toString() {
		return "Candidates [candidates=" + Arrays.toString(candidates) + "]";
	}
}