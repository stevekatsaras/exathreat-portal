package com.exathreat.organisation.insights.visualize.enums;

import lombok.Getter;

@Getter
public enum QueryAggregateEnum {
	DATEHISTOGRAM("datehistogram", "Date Histogram", "date"),
	TERMS("terms", "Terms", "text");

	private final String type;
	private final String label;
	private final String dataType;

	QueryAggregateEnum(String type, String label, String dataType) {
		this.type = type;
		this.label = label;
		this.dataType = dataType;
	}
	
	public static QueryAggregateEnum getByType(String type) {
		QueryAggregateEnum foundQueryAggregate = null;
		for (QueryAggregateEnum queryAggregate : QueryAggregateEnum.values()) {
			if (type.equalsIgnoreCase(queryAggregate.getType())) {
				foundQueryAggregate = queryAggregate;
				break;
			}
		}
		return foundQueryAggregate;
	}
	
}