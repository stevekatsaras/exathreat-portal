package com.exathreat.organisation.insights.visualize;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class RunVisualizeDashboardChartData {
	private String dateRange;
	private String dateRangeFormat;
	private String chartCode;
	private Map<String, Object> panelMap;

	/**
	 * Series results ... variables include:
	 * 1. dataRangeInfo
	 * 2. searchResults
	 */

	/**
	 * keys included in this map are:
	 * "gte"					- ZonedDateTime.class
	 * "lte"					- ZonedDateTime.class
	 * "timeInterval"	- TimeInterval.class
	 */
	private Map<String, Object> dateRangeInfo;

	/**
	 * "labels"	- List.class
	 * "series"	- List.class
	 */
	private Map<String, Object> searchResults;
}