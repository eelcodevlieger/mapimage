package com.knocksfornometer.mapimage.data;


public enum ElectionYearDataSource{
	_2010ElectoralCommission(ElectionYear._2010, ElectionDataSource.ElectoralCommission),
	_2015ElectoralCommission(ElectionYear._2015, ElectionDataSource.ElectoralCommission),
	_2015Guardian(ElectionYear._2015, ElectionDataSource.Guardian);
	
	private final ElectionYear electionYear;
	private final ElectionDataSource electionDataSource;
	
	private ElectionYearDataSource(ElectionYear electionYear, ElectionDataSource electionDataSource){
		this.electionYear = electionYear;
		this.electionDataSource = electionDataSource;
	}

	public ElectionYear getElectionYear() {
		return electionYear;
	}

	public ElectionDataSource getElectionDataSource() {
		return electionDataSource;
	}
}