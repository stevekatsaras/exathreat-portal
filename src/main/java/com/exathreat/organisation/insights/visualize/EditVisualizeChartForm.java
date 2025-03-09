package com.exathreat.organisation.insights.visualize;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.exathreat.common.jpa.entity.OrganisationVisualization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class EditVisualizeChartForm {
	@Valid
	private OrganisationVisualization organisationVisualization;

	/**
	 * Query variables ... variables include:
	 * 1. queries
	 */

	private List<Map<String, Object>> queries;
	
	/**
	 * Chart variables 
	 */

	private String chartCode;
	
	@NotEmpty(message = "Chart type is required")
	private String chartType;

	private String chartBackground;
	private String chartForeground;
	private String chartThemeMode;
	private String chartPalette;

	private String chartTitle;
	private String chartTitleAlign;
	private Integer chartTitleMargin;
	private Integer chartTitleOffsetX;
	private Integer chartTitleOffsetY;
	private String chartTitleColor;

	private Boolean chartGridShow;
	private String chartGridColor;
	private Boolean chartGridShowX;
	private Boolean chartGridShowY;
	private String chartGridPosition;

	private Boolean chartDataLabelsShow;

	private Boolean chartLegendShow;
	private String chartLegendAlign;
	private String chartLegendPosition;
	private Integer chartLegendOffsetX;
	private Integer chartLegendOffsetY;
	private Integer chartLegendIconWidth;
	private Integer chartLegendIconHeight;
	private Integer chartLegendIconRadius;
	private Integer chartLegendIconOffsetX;
	private Integer chartLegendIconOffsetY;

	private Integer chartMarkerSize;
	private String chartMarkerShape;
	private Integer chartMarkerRadius;
	private Integer chartMarkerOffsetX;
	private Integer chartMarkerOffsetY;

	private Boolean chartStrokeShow;
	private String chartStrokeCurve;
	private Integer chartStrokeWidth;

	private Boolean chartTooltipEnabled;

	private Boolean chartXAxisShow;
	private String chartXAxisType;
	private String chartXAxisTitle;
	private Integer chartXAxisTitleOffsetX;
	private Integer chartXAxisTitleOffsetY;

	private Boolean chartYAxisShow;
	private Boolean chartYAxisOpposite;	// false = left, true = right
	private Boolean chartYAxisFlip;			// false = bottom, true = top
	private Integer chartYAxisDecimals;
	private String chartYAxisTitle;
	private Integer chartYAxisTitleOffsetX;
	private Integer chartYAxisTitleOffsetY;

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