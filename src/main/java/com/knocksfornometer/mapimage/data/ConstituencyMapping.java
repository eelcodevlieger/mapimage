package com.knocksfornometer.mapimage.data;

import java.util.Map;

import com.knocksfornometer.mapimage.ConstituencyKeyGenerator;

public class ConstituencyMapping implements ConstituencyKeyGenerator{

	private final String[] prefixes;
	private final Map<String, String> constituencyNameMapping;

	public ConstituencyMapping(String[] prefixes, Map<String, String> constituencyNameMapping) {
		this.prefixes = prefixes;
		this.constituencyNameMapping = constituencyNameMapping;
	}
	
	/**
	 * Builds a key from the constituencyName, converting to UPPERCASE ASCII and removing spaces.
	 * {@link #PREFIXES} holds a list of prefixes which will be changed to suffix in the key (WESTBRISTOL -> BRISTOLWEST).
	 * {@link #constituencyNameMapping} allows for manual key overrides loaded from the {@value #CONSTITUENCY_NAME_MAPPING_FILE} config file
	 */
	public String toKey(String constituencyName) {
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
}