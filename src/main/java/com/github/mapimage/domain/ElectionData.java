package com.github.mapimage.domain;

import java.util.Map;

import com.github.mapimage.data.ElectionYearDataSource;

public record ElectionData(ElectionYearDataSource electionYearDataSource,
						   Map<String, String> partyColorMapping,
						   String svgMapInputFile,
						   ConstituencyKeyGenerator constituencyKeyGenerator) {

}