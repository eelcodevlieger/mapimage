package com.knocksfornometer.mapimage.data;

import java.util.Map;

import com.knocksfornometer.mapimage.ConstituencyKeyGenerator;

public class ConstituencyMapping implements ConstituencyKeyGenerator{

	private final String[] prefixes;
	private final Map<String, String> constituencyNameMapping;
	private final Map<String, String> seatNumberToConstituencyNameMapping;

	public ConstituencyMapping(final String[] prefixes, final Map<String, String> constituencyNameMapping, final Map<String, String> seatNumberToConstituencyNameMapping) {
		this.prefixes = prefixes;
		this.constituencyNameMapping = constituencyNameMapping;
		this.seatNumberToConstituencyNameMapping = seatNumberToConstituencyNameMapping;
	}
	
	/**
	 * Builds a key from the constituencyName, converting to UPPERCASE ASCII and removing spaces.
	 * {@link #prefixes} holds a list of prefixes which will be changed to suffix in the key (WESTBRISTOL -> BRISTOLWEST).
	 * {@link #constituencyNameMapping} allows for manual key overrides
	 */
	public String toKey(String constituencyName) {
		// 2005 data mapping
		if( constituencyName.startsWith("seat-") )
			constituencyName = convertSeatNumberToConstituencyName(constituencyName);
		
		// remove all non-word characters and UPPER CASE the result
		constituencyName = constituencyName.replaceAll("&", "AND").replaceAll("\\W", "").replaceAll("_", "").toUpperCase();
		
		// Specific prefix 'hack' to convert WESTBRISTOL in to BRISTOLWEST for example
		// Due to data and SVG using different name conventions
		for (String prefix : prefixes) {
			if(constituencyName.startsWith(prefix)){
				constituencyName = constituencyName.substring(prefix.length()) + prefix;
				break;
			}
		}
		
		// check key override map
		String keyMappingOverride = constituencyNameMapping.get(constituencyName);
		
		return keyMappingOverride != null ? keyMappingOverride : constituencyName;
	}

	private String convertSeatNumberToConstituencyName(final String seatNumber) {
		String constituencyName = seatNumberToConstituencyNameMapping.get(seatNumber);
		return constituencyName != null ? constituencyName : seatNumber;
	}
}