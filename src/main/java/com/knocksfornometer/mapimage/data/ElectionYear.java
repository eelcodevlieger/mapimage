package com.knocksfornometer.mapimage.data;

public enum ElectionYear {
	_2005, _2010, _2015, _2017, _2019;

	private final int year;

	ElectionYear(){
		if(!this.name().matches("_\\d{4}")){
			System.err.println("Invalid enum name year format");
			System.exit(-1);
		}
		this.year = Integer.parseInt(this.name().substring(1));
	}
	
	public int getYear() {
		return year;
	}

	public static ElectionYear get(int year){
		return ElectionYear.valueOf("_" + year);
	}
}