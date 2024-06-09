package com.knocksfornometer.mapimage.domain;

import java.util.Map;

import com.knocksfornometer.mapimage.data.ElectionYearDataSource;

/**
 * @author Eelco de Vlieger
 */
public record ElectionData(ElectionYearDataSource electionYearDataSource,
						   Map<String, Candidates> constituencyNameToPartyCandidates,
						   String svgMapInputFile,
						   ConstituencyKeyGenerator constituencyKeyGenerator) {


}