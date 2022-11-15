package com.knocksfornometer.mapimage.data;


import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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


	ElectionYearDataSource(ElectionYear electionYear, ElectionDataSource electionDataSource){
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
}