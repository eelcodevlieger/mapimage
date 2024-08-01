package com.github.mapimage.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public class CandidateResults {

	private final CandidateResult[] candidateResults;
	/** Candidates election result - representation: percentage as array of size 100 (filled with {@link CandidateResult} references by that ratio) */
	private final CandidateResult[] candidateResultsExpanded;

	public CandidateResults(CandidateResult[] candidateResults) {
		this.candidateResults = validate(candidateResults);
		this.candidateResultsExpanded = expand(candidateResults);
	}

	private CandidateResult[] expand(final CandidateResult[] candidateResults) {
		final CandidateResult[] candidatesExpanded = new CandidateResult[100];
		int index = 0;
		for (CandidateResult candidateResult : candidateResults)
			for (int i = 0; i < candidateResult.getPercentageOfTotalElectorate(); i++)
				candidatesExpanded[index++] = candidateResult;
		return candidatesExpanded;
	}

	private CandidateResult[] validate(final CandidateResult[] candidateResults) {
		int totalPercentage = 0;
		for (CandidateResult candidateResult : candidateResults)
			totalPercentage += candidateResult.getPercentageOfTotalElectorate();
		if(totalPercentage != 100)
			throw new IllegalStateException("Candidates percentage should sum to 100");
		return candidateResults;
	}

	@Override
	public String toString() {
		return "Candidates [candidates=" + Arrays.toString(candidateResults) + "]";
	}
}