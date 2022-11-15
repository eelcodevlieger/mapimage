package com.knocksfornometer.mapimage.data;

import java.util.Map;

import com.knocksfornometer.mapimage.Candidates;
import com.knocksfornometer.mapimage.ConstituencyKeyGenerator;

/**
 * @author Eelco de Vlieger
 */
public record ElectionData(ElectionYearDataSource electionYearDataSource,
						   Map<String, Candidates> constituencyNameToPartyCandidates,
						   String svgMapInputFile,
						   ConstituencyKeyGenerator constituencyKeyGenerator) {


}