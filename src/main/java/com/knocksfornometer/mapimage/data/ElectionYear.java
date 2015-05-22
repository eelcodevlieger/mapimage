package com.knocksfornometer.mapimage.data;

public enum ElectionYear {
	_2010(2010), _2015(2015);

	private final int year;
	private ElectionYear(int year){
		this.year = year;
		
	}
	
	public int getYear() {
		return year;
	}
}