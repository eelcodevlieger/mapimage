package com.knocksfornometer.mapimage.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum ElectionYear {
	_2005, _2010, _2015, _2017, _2019;

	public static final String YEAR_PATTERN = "_\\d{4}";
	private final int year;

	ElectionYear(){
		if(!this.name().matches(YEAR_PATTERN)){
			System.err.format("Invalid enum name year format [input=%s, expectedFormat=%s]\n", this.name(), YEAR_PATTERN);
			System.exit(-1);
		}
		this.year = Integer.parseInt(this.name().substring(1));
	}

	public static ElectionYear get(int year){
		return ElectionYear.valueOf("_" + year);
	}
}