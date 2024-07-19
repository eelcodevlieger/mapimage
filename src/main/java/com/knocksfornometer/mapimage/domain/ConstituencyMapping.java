package com.knocksfornometer.mapimage.domain;

import java.util.Map;

import com.knocksfornometer.mapimage.domain.ConstituencyKeyGenerator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConstituencyMapping implements ConstituencyKeyGenerator{

	private final String[] prefixes;
	private final Map<String, String> constituencyNameMapping;
	private final Map<String, String> seatNumberToConstituencyNameMapping;

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
		constituencyName = constituencyName.replaceAll("&", "AND")
				.replaceAll("x2C", "") // part of hex code for comma (0x2C) found as string in path name - removing
				.replaceAll("x27", "") // part of hex code for apostrophe (0x27) found as string in path name - removing
				.replaceAll("[\\W_,\\d]", "") // remove all underscores, numbers and non-word characters
				.toUpperCase();
		
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