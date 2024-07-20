package com.github.mapimage.data;


import com.github.mapimage.data._2005.electoralcommission._2005ElectoralCommissionElectionData;
import com.github.mapimage.data._2010.electoralcommission._2010ElectoralCommissionElectionData;
import com.github.mapimage.data._2015.electoralcommission._2015ElectoralCommissionElectionData;
import com.github.mapimage.data._2017.electoralcommission._2017ElectoralCommissionElectionData;
import com.github.mapimage.data._2019.ukparliament._2019UkParliamentElectionData;
import com.github.mapimage.data._2024.ukparliament._2024UkParliamentElectionData;
import com.github.mapimage.domain.ElectionYear;
import lombok.Getter;

import java.util.*;

import static com.github.mapimage.data.ElectionDataSource.*;

@Getter
public enum ElectionYearDataSource{
	_2005ElectoralCommission(ElectionYear._2005, ELECTORAL_COMMISSION, new _2005ElectoralCommissionElectionData()),
	_2010ElectoralCommission(ElectionYear._2010, ELECTORAL_COMMISSION, new _2010ElectoralCommissionElectionData()),
	_2015ElectoralCommission(ElectionYear._2015, ELECTORAL_COMMISSION, new _2015ElectoralCommissionElectionData()),
	_2017ElectoralCommission(ElectionYear._2017, ELECTORAL_COMMISSION, new _2017ElectoralCommissionElectionData()),
	_2019UkParliament(ElectionYear._2019, UK_PARLIAMENT, new _2019UkParliamentElectionData()),
	_2024UkParliament(ElectionYear._2024, UK_PARLIAMENT, new _2024UkParliamentElectionData());

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
	private final ElectionDataLoader electionDataLoader;


	ElectionYearDataSource(final ElectionYear electionYear, final ElectionDataSource electionDataSource, final ElectionDataLoader electionDataLoader){
		this.electionYear = electionYear;
		this.electionDataSource = electionDataSource;
		this.electionDataLoader = electionDataLoader;
	}

	public static List<ElectionYearDataSource> getElectionYearDataSourceByYear(final ElectionYear electionYear) {
		return electionYearDataSourceByYear.get(electionYear);
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