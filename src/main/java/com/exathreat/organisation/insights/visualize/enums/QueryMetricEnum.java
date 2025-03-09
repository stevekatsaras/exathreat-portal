package com.exathreat.organisation.insights.visualize.enums;

import lombok.Getter;

@Getter
public enum QueryMetricEnum {
	AVERAGE("average", "Average", "long"),
	COUNT("count", "Count", "long"),
	MAX("max", "Max", "long"),
	MIN("min", "Min", "long"),
	SUM("sum", "Sum", "long");

	private final String type;
	private final String label;
	private final String dataType;

	QueryMetricEnum(String type, String label, String dataType) {
		this.type = type;
		this.label = label;
		this.dataType = dataType;
	}
	
	public static QueryMetricEnum getByType(String type) {
		QueryMetricEnum foundQueryMetric = null;
		for (QueryMetricEnum queryMetric : QueryMetricEnum.values()) {
			if (type.equalsIgnoreCase(queryMetric.getType())) {
				foundQueryMetric = queryMetric;
				break;
			}
		}
		return foundQueryMetric;
	}
}
