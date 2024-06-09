package com.knocksfornometer.mapimage.data;


import com.knocksfornometer.mapimage.domain.ElectionYear;

import java.util.*;

public enum ElectionYearDataSource{
	_2005ElectoralCommission(ElectionYear._2005, ElectionDataSource.ElectoralCommission),
	_2010ElectoralCommission(ElectionYear._2010, ElectionDataSource.ElectoralCommission),
	_2015ElectoralCommission(ElectionYear._2015, ElectionDataSource.ElectoralCommission),
	_2017ElectoralCommission(ElectionYear._2017, ElectionDataSource.ElectoralCommission),
	_2019UkParliament(ElectionYear._2019, ElectionDataSource.UkParliament);

	private static final Map<ElectionYear, List<ElectionYearDataSource>> electionYearDataSourceByYear = new EnumMap<>(ElectionYear.class);
	static {
		for(ElectionYearDataSource electionYearDataSource : ElectionYearDataSource.values()) {
			final var electionYear = electionYearDataSource.electionYear;
			var electionDataSources = electionYearDataSourceByYear.get(electionYear);
			if(electionDataSources == null){
				electionDataSources = new ArrayList<>();
				electionDataSources.add(electionYearDataSource);
				electionYearDataSourceByYear.put(electionYear, electionDataSources);
			}
		}
	}

	private final ElectionYear electionYear;
	private final ElectionDataSource electionDataSource;


	ElectionYearDataSource(final ElectionYear electionYear, final ElectionDataSource electionDataSource){
		this.electionYear = electionYear;
		this.electionDataSource = electionDataSource;
	}

	public static List<ElectionYearDataSource> getElectionYearDataSourceByYear(final ElectionYear electionYear) {
		return electionYearDataSourceByYear.get(electionYear);
	}

	public ElectionYear getElectionYear() {
		return electionYear;
	}

	public ElectionDataSource getElectionDataSource() {
		return electionDataSource;
	}

	/**
	 * Default to all ElectionYearDataSource values when no specific ones are requested.
	 */
	public static ElectionYearDataSource[] getElectionYearDataSources(final String... electionYears) {
		if(electionYears == null || electionYears.length == 0){
			return ElectionYearDataSource.values();
		}

		return Arrays.stream(electionYears)
				.map(Integer::valueOf)
				.map(ElectionYear::get)
				.map(ElectionYearDataSource::getElectionYearDataSourceByYear)
				.flatMap(Collection::stream)
				.toArray(ElectionYearDataSource[]::new);
	}

}