package com.exathreat.organisation.forensics.threats;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ThreatsForensicsPanelData {
	private String dateRange;
	private String dateRangeFormat;

	/**
	 * keys included in this map are:
	 * "gte"					- ZonedDateTime.class
	 * "lte"					- ZonedDateTime.class
	 * "timeInterval"	- TimeInterval.class
	 */
	private Map<String, Object> dateRangeInfo;

	/**
	 * "labels"						- List.class
	 * "series"						- List.class
	 * "geoIps"						- List.class
	 * "tookInMs"					- Long.class
	 * "resultsSizeTotal"	- Integer.class
	 */
	private Map<String, Object> searchResults;
}