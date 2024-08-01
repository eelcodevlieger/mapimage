package com.github.mapimage.domain;

import java.awt.Color;
import java.util.Collection;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class CandidateResult {

	private final int percentageOfTotalElectorate;
	private final Party party;

	public CandidateResult(final Party party, final int percentageOfTotalElectorate) {
		this.percentageOfTotalElectorate = percentageOfTotalElectorate;
		this.party = party;
		Preconditions.checkArgument(percentageOfTotalElectorate >= 0 && percentageOfTotalElectorate <= 100, "percentageOfTotalElectorate should be in range 0 to 100 [percentageOfTotalElectorate=" + percentageOfTotalElectorate + "]");
	}

	/**
	 * @param candidateResults - All candidates (and their vote as percentage of the total electorate) of a constituency
	 * @return Create a CandidateResult to represent the 'no vote' share of the population.
	 */
	public static CandidateResult createNoVoteCandidate(final Collection<CandidateResult> candidateResults) {
		final int totalPercentage = candidateResults
				.stream()
				.map(CandidateResult::getPercentageOfTotalElectorate)
				.mapToInt(Integer::intValue)
				.sum();
		return new CandidateResult(Party.NO_VOTE, 100 - totalPercentage);
	}
	
	@Override
	public String toString() {
		return "Candidate [party=" + party + ", percentageOfTotalElectorate=" + percentageOfTotalElectorate + "]";
	}
}