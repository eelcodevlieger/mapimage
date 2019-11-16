package com.knocksfornometer.mapimage.data;


public enum ElectionYearDataSource{
	_2005ElectoralCommission(ElectionYear._2005, ElectionDataSource.ElectoralCommission),
	_2010ElectoralCommission(ElectionYear._2010, ElectionDataSource.ElectoralCommission),
	_2015ElectoralCommission(ElectionYear._2015, ElectionDataSource.ElectoralCommission),
	_2017ElectoralCommission(ElectionYear._2017, ElectionDataSource.ElectoralCommission);
	
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