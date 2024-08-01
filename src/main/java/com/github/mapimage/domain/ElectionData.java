package com.github.mapimage.domain;

import java.util.Map;

import com.github.mapimage.data.ElectionYearDataSource;

public record ElectionData(ElectionYearDataSource electionYearDataSource,
						   String svgMapInputFile,
						   ConstituencyKeyGenerator constituencyKeyGenerator) {

}