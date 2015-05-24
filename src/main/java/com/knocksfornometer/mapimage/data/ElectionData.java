package com.knocksfornometer.mapimage.data;

import java.util.Map;

import com.knocksfornometer.mapimage.Candidates;
import com.knocksfornometer.mapimage.ConstituencyKeyGenerator;

/**
 * 
 * @author Eelco de Vlieger
 */
public class ElectionData {

	private final ElectionYearDataSource electionYearDataSource;
	private final Map<String, Candidates> electionDataMap;
	private final ConstituencyKeyGenerator constituencyKeyGenerator;
	private final String svgMapInputFile;

	public ElectionData(ElectionYearDataSource electionYearDataSource, Map<String, Candidates> electionDataMap, String svgMapInputFile, ConstituencyKeyGenerator constituencyKeyGenerator) {
		this.electionYearDataSource = electionYearDataSource;
		this.electionDataMap = electionDataMap;
		this.svgMapInputFile = svgMapInputFile;
		this.constituencyKeyGenerator = constituencyKeyGenerator;
	}

	public ElectionYearDataSource getElectionYearDataSource() {
		return electionYearDataSource;
	}
	
	public Map<String, Candidates> getElectionDataMap() {
		return electionDataMap;
	}

	public ConstituencyKeyGenerator getConstituencyKeyGenerator() {
		return constituencyKeyGenerator;
	}

	public String getSvgMapInputFile() {
		return svgMapInputFile;
	}
}