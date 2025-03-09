package com.exathreat.organisation.insights.explore;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ExploreInsightsData {
	private String queryStr;
	private String indexAlias;
	private String dateRange;
	private String dateRangeFormat;
	private String intervalUnit;
	private Long minDocCount;
	private String scrollId;

	/**
	 * keys included in this map are:
	 * "gte"					- ZonedDateTime.class
	 * "lte"					- ZonedDateTime.class
	 * "timeInterval"	- TimeInterval.class
	 */
	private Map<String, Object> dateRangeInfo;

	/**
	 * "scrollId"					- String.class
	 * "series"						- List.class
	 * "tookInMs"					- Long.class
	 * "resultsSizeTotal"	- Integer.class
	 * "resultSize"				- Integer.class
	 * "results"					- List.class
	 * "batchSize"				- Integer.class
	 */
	private Map<String, Object> searchResults;
}