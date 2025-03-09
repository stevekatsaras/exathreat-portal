package com.exathreat.organisation.insights.visualize;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.exathreat.common.config.factory.ElasticsearchFactory;
import com.exathreat.common.jpa.entity.OrganisationVisualization;
import com.exathreat.common.jpa.repository.OrganisationVisualizationRepository;
import com.exathreat.common.service.TimeSeriesSearchService;
import com.exathreat.common.support.HackSupport;
import com.exathreat.common.support.TimestampSupport;
import com.exathreat.organisation.insights.visualize.enums.QueryAggregateEnum;
import com.exathreat.organisation.insights.visualize.enums.QueryMetricEnum;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram.ParsedBucket;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedSingleValueNumericMetricsAggregation;
import org.elasticsearch.search.aggregations.support.ValuesSourceAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;

@Service
public class RunVisualizeDashboardService {

	@Autowired
	private ElasticsearchFactory elasticsearchFactory;

	@Autowired
	private HackSupport hackSupport;

	@Autowired
	private OrganisationVisualizationRepository organisationVisualizationRepository;

	@Autowired
	private TimeSeriesSearchService timeSeriesSearchService;

	@Autowired
	private TimestampSupport timestampSupport;

	@Transactional(readOnly = false)
	public void save(String orgCode, String vizCode, Map<String, Object> params, ModelMap modelMap) throws Exception {
		OrganisationVisualization organisationVisualization = organisationVisualizationRepository.findByVizCode(vizCode);
		organisationVisualization.setDateRange((String) params.get("dateRange"));
		organisationVisualization.setDateFormat((String) params.get("dateFormat"));
		organisationVisualization.setTimeFormat((String) params.get("timeFormat"));
		organisationVisualization.setRefresh((String) params.get("refreshUnit"));
		organisationVisualization.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationVisualizationRepository.saveAndFlush(organisationVisualization);
	}

	@Transactional(readOnly = false)
	@SuppressWarnings("unchecked")
	public void deleteChart(String orgCode, String vizCode, String chartCode, ModelMap modelMap) throws Exception {
		OrganisationVisualization organisationVisualization = organisationVisualizationRepository.findByVizCode(vizCode);

		List<Map<String, Object>> panels = (List<Map<String, Object>>) organisationVisualization.getCharts().get("panels");
		for (Map<String, Object> panel : panels) {
			if (chartCode.equals(panel.get("chartCode"))) {
				panels.remove(panel);
				break;
			}
		}
		
		organisationVisualization.setModified(ZonedDateTime.now(ZoneOffset.UTC));
		organisationVisualizationRepository.saveAndFlush(organisationVisualization);
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public RunVisualizeDashboardChartData loadData(String orgCode, String vizCode, RunVisualizeDashboardChartData runVisualizeDashboardChartData, ModelMap modelMap) throws Exception {
		try {
			OrganisationVisualization organisationVisualization = organisationVisualizationRepository.findByVizCode(vizCode);

			List<Map<String, Object>> panels = (List<Map<String, Object>>) organisationVisualization.getCharts().get("panels");
			Map<String, Object> panelMap = null;
			for (Map<String, Object> panel : panels) {
				if (runVisualizeDashboardChartData.getChartCode().equals(panel.get("chartCode"))) {
					panelMap = panel;
					break;
				}
			}

			Map<String, Object> dateRangeInfo = timeSeriesSearchService.getDateRangeInfo(
				runVisualizeDashboardChartData.getDateRange(), 
				runVisualizeDashboardChartData.getDateRangeFormat(), 
				"auto", 
				modelMap);

			if (!dateRangeInfo.isEmpty()) {
				runVisualizeDashboardChartData.setPanelMap(panelMap);
				runVisualizeDashboardChartData.setDateRangeInfo(dateRangeInfo);

				switch ((String) panelMap.get("chartType")) {
					case "area":
						runVisualizeDashboardChartData.setSearchResults(runQueriesForLinearCharts(runVisualizeDashboardChartData, dateRangeInfo, modelMap));
						break;
					case "bar":
						runVisualizeDashboardChartData.setSearchResults(runQueriesForLinearCharts(runVisualizeDashboardChartData, dateRangeInfo, modelMap));
						break;
					case "line":
						runVisualizeDashboardChartData.setSearchResults(runQueriesForLinearCharts(runVisualizeDashboardChartData, dateRangeInfo, modelMap));
						break;
					case "treemap":
						runVisualizeDashboardChartData.setSearchResults(runQueriesForLinearCharts(runVisualizeDashboardChartData, dateRangeInfo, modelMap));
						break;
					case "pie":
						runVisualizeDashboardChartData.setSearchResults(runQueryForCircularCharts(runVisualizeDashboardChartData, dateRangeInfo, modelMap));
						break;
					case "donut":
						runVisualizeDashboardChartData.setSearchResults(runQueryForCircularCharts(runVisualizeDashboardChartData, dateRangeInfo, modelMap));
						break;
					case "polarArea":
						runVisualizeDashboardChartData.setSearchResults(runQueryForCircularCharts(runVisualizeDashboardChartData, dateRangeInfo, modelMap));
						break;
					default:
						break;
				}
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		return runVisualizeDashboardChartData;
	}

	/**
	 * runs any queries that are for 'linear' charts
	 * - area, bar, column, line
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> runQueriesForLinearCharts(RunVisualizeDashboardChartData runVisualizeDashboardChartData, Map<String, Object> dateRangeInfo, ModelMap modelMap) throws Exception {
		Map<String, Object> panelMap = runVisualizeDashboardChartData.getPanelMap();
		List<Map<String, Object>> queries = (List<Map<String, Object>>) panelMap.get("queries");

		List<Map<String, Object>> series = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> query : queries) {
			if (Boolean.parseBoolean((String) query.get("queryShow"))) {
				if (QueryAggregateEnum.DATEHISTOGRAM.getType().equals(query.get("queryAggregateType"))) {
					series.add(runDateHistogramQueryForLinearChart(query, dateRangeInfo, modelMap));
				}
				else if (QueryAggregateEnum.TERMS.getType().equals(query.get("queryAggregateType"))) {
					series.add(runTermQueryForLinearChart(query, dateRangeInfo));
				}
			}
		}
		return Map.of("labels", new ArrayList<Object>(), "series", series);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> runDateHistogramQueryForLinearChart(Map<String, Object> query, Map<String, Object> dateRangeInfo, ModelMap modelMap) throws Exception {
		List<List<Object>> buckets = new ArrayList<List<Object>>();

		SearchResponse searchResponse = searchByDateHistogram(query, dateRangeInfo);
		ParsedDateHistogram parsedDateHistogram = (ParsedDateHistogram) searchResponse.getAggregations().asMap().get("aggsByDateHistogram");
		for (ParsedBucket parsedBucket : (List<ParsedBucket>) parsedDateHistogram.getBuckets()) {
			OffsetDateTime utctz = hackSupport.hackUTCOnZoneDateTime((ZonedDateTime) parsedBucket.getKey(), (String) modelMap.get("timezone"));
			Object yPoint = parsedBucket.getDocCount();
			if (!parsedBucket.getAggregations().asList().isEmpty()) {
				ParsedSingleValueNumericMetricsAggregation psvnma = (ParsedSingleValueNumericMetricsAggregation) parsedBucket.getAggregations().asList().get(0);
				yPoint = (Double.isInfinite(psvnma.value()) ? 0.0 : psvnma.value());
			}
			buckets.add(List.of(utctz.toInstant().toEpochMilli(), yPoint));
		}
		return Map.of("name", constructQueryNameForSeries(query), "data", buckets);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> runTermQueryForLinearChart(Map<String, Object> query, Map<String, Object> dateRangeInfo) throws Exception {
		List<Map<String, Object>> buckets = new ArrayList<Map<String, Object>>();

		SearchResponse searchResponse = searchByTerms(query, dateRangeInfo);
		ParsedTerms parsedTerms = (ParsedTerms) searchResponse.getAggregations().asMap().get("aggsByTerm");
		for (ParsedTerms.ParsedBucket parsedBucket : (List<ParsedTerms.ParsedBucket>) parsedTerms.getBuckets()) {
			Object xLabel = parsedBucket.getKeyAsString();
			Object yPoint = parsedBucket.getDocCount();
			if (!parsedBucket.getAggregations().asList().isEmpty()) {
				ParsedSingleValueNumericMetricsAggregation psvnma = (ParsedSingleValueNumericMetricsAggregation) parsedBucket.getAggregations().asList().get(0);
				yPoint = (Double.isInfinite(psvnma.value()) ? 0.0 : psvnma.value());
			}
			buckets.add(Map.of("x", xLabel, "y", yPoint));		
		}
		return Map.of("name", constructQueryNameForSeries(query), "data", buckets);
	}

	/**
	 * runs any queries that are for 'circular' charts
	 * - donut, pie, radialbar, polararea
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> runQueryForCircularCharts(RunVisualizeDashboardChartData runVisualizeDashboardChartData, Map<String, Object> dateRangeInfo, ModelMap modelMap) throws Exception {
		Map<String, Object> panelMap = runVisualizeDashboardChartData.getPanelMap();
		List<Map<String, Object>> queries = (List<Map<String, Object>>) panelMap.get("queries");

		Map<String, Object> results = null;
		for(Map<String, Object> query : queries) {
			if (Boolean.parseBoolean((String) query.get("queryShow"))) {
				if (QueryAggregateEnum.DATEHISTOGRAM.getType().equals(query.get("queryAggregateType"))) {
					results = runDateHistogramQueryForCircularChart(query, dateRangeInfo, modelMap);
				}
				else if (QueryAggregateEnum.TERMS.getType().equals(query.get("queryAggregateType"))) {
					results = runTermQueryForCircularChart(query, dateRangeInfo);
				}
				break;
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> runDateHistogramQueryForCircularChart(Map<String, Object> query, Map<String, Object> dateRangeInfo, ModelMap modelMap) throws Exception {
		List<Object> labels = new ArrayList<Object>();
		List<Object> series = new ArrayList<Object>();

		SearchResponse searchResponse = searchByDateHistogram(query, dateRangeInfo);
		ParsedDateHistogram parsedDateHistogram = (ParsedDateHistogram) searchResponse.getAggregations().asMap().get("aggsByDateHistogram");
		for (ParsedBucket parsedBucket : (List<ParsedBucket>) parsedDateHistogram.getBuckets()) {
			labels.add(timestampSupport.format(
				(ZonedDateTime) parsedBucket.getKey(), 
				(String) modelMap.get("timezone"), 
				(String) modelMap.get("javaDateFormat") + " " + (String) modelMap.get("javaTimeFormat")));
			
			if (parsedBucket.getAggregations().asList().isEmpty()) {
				series.add(parsedBucket.getDocCount());
			}
			else {
				ParsedSingleValueNumericMetricsAggregation psvnma = (ParsedSingleValueNumericMetricsAggregation) parsedBucket.getAggregations().asList().get(0);
				series.add((Double.isInfinite(psvnma.value()) ? 0.0 : psvnma.value()));
			}
		}
		return Map.of("labels", labels, "series", series);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> runTermQueryForCircularChart(Map<String, Object> query, Map<String, Object> dateRangeInfo) throws Exception {
		List<Object> labels = new ArrayList<Object>();
		List<Object> series = new ArrayList<Object>();

		SearchResponse searchResponse = searchByTerms(query, dateRangeInfo);
		ParsedTerms parsedTerms = (ParsedTerms) searchResponse.getAggregations().asMap().get("aggsByTerm");
		for (ParsedTerms.ParsedBucket parsedBucket : (List<ParsedTerms.ParsedBucket>) parsedTerms.getBuckets()) {
			labels.add(parsedBucket.getKeyAsString());
			if (parsedBucket.getAggregations().asList().isEmpty()) {
				series.add(parsedBucket.getDocCount());
			}
			else {
				ParsedSingleValueNumericMetricsAggregation psvnma = (ParsedSingleValueNumericMetricsAggregation) parsedBucket.getAggregations().asList().get(0);
				series.add((Double.isInfinite(psvnma.value()) ? 0.0 : psvnma.value()));
			}
		}
		return Map.of("labels", labels, "series", series);
	}

	@SuppressWarnings("unchecked")
	private SearchResponse searchByDateHistogram(Map<String, Object> query, Map<String, Object> dateRangeInfo) throws Exception {
		ZonedDateTime gte = (ZonedDateTime) dateRangeInfo.get("gte");
		ZonedDateTime lte = (ZonedDateTime) dateRangeInfo.get("lte");
		Map<String, Object> timeInterval = (Map<String, Object>) dateRangeInfo.get("timeInterval");

		DateHistogramAggregationBuilder dateHistogramAggregationBuilder = AggregationBuilders.dateHistogram("aggsByDateHistogram")
			.field((String) query.get("queryAggregateField"))
			.fixedInterval((DateHistogramInterval) timeInterval.get("interval"))
			.extendedBounds(new ExtendedBounds(gte.toInstant().toEpochMilli(), lte.toInstant().toEpochMilli()))
			.minDocCount(Long.parseLong((String) query.get("queryMinDocCount")));
		
		addMetricSubAggregation(dateHistogramAggregationBuilder, query);

		SearchSourceBuilder searchSourceBuilder = getSearchRequest(gte, lte, (String) query.get("queryStr"));
		searchSourceBuilder.aggregation(dateHistogramAggregationBuilder);
		return getSearchResponse((String) query.get("queryIndexAlias"), searchSourceBuilder);
	}

	private SearchResponse searchByTerms(Map<String, Object> query, Map<String, Object> dateRangeInfo) throws Exception {
		TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("aggsByTerm")
			.field((String) query.get("queryAggregateField"))
			.minDocCount(Long.parseLong((String) query.get("queryMinDocCount")))
			.size(Integer.parseInt((String) query.get("queryLimit")))
			.order(getBucketOrder(query));

		addMetricSubAggregation(termsAggregationBuilder, query);

		SearchSourceBuilder searchSourceBuilder = getSearchRequest((ZonedDateTime) dateRangeInfo.get("gte"), (ZonedDateTime) dateRangeInfo.get("lte"), (String) query.get("queryStr"));
		searchSourceBuilder.aggregation(termsAggregationBuilder);
		
		return getSearchResponse((String) query.get("queryIndexAlias"), searchSourceBuilder);
	}

	private String constructQueryNameForSeries(Map<String, Object> query) {
		String queryNameForSeries = (String) query.get("queryName");
		if (!StringUtils.hasText(queryNameForSeries)) {
			QueryMetricEnum queryMetricEnum = QueryMetricEnum.getByType((String) query.get("queryMetricType"));

			queryNameForSeries = queryMetricEnum.getLabel();
			if (queryMetricEnum != QueryMetricEnum.COUNT) {
				queryNameForSeries += " - " + (String) query.get("queryMetricField");
			}
		}
		return queryNameForSeries;
	}

	private void addMetricSubAggregation(ValuesSourceAggregationBuilder valuesSourceAggregationBuilder, Map<String, Object> query) {
		QueryMetricEnum queryMetricEnum = QueryMetricEnum.getByType((String) query.get("queryMetricType"));

		if (queryMetricEnum == QueryMetricEnum.AVERAGE) {
			valuesSourceAggregationBuilder.subAggregation(AggregationBuilders.avg("aggsByMetric").field((String) query.get("queryMetricField")));
		}
		else if (queryMetricEnum == QueryMetricEnum.MAX) {
			valuesSourceAggregationBuilder.subAggregation(AggregationBuilders.max("aggsByMetric").field((String) query.get("queryMetricField")));
		}
		else if (queryMetricEnum == QueryMetricEnum.MIN) {
			valuesSourceAggregationBuilder.subAggregation(AggregationBuilders.min("aggsByMetric").field((String) query.get("queryMetricField")));
		}
		else if (queryMetricEnum == QueryMetricEnum.SUM) {
			valuesSourceAggregationBuilder.subAggregation(AggregationBuilders.sum("aggsByMetric").field((String) query.get("queryMetricField")));
		}
	}

	private BucketOrder getBucketOrder(Map<String, Object> query) {
		BucketOrder bucketOrder = null;

		String queryOrder = (String) query.get("queryOrder");
		String queryOrderBy = (String) query.get("queryOrderBy");
		switch (queryOrderBy) {
			case "metric": {
				QueryMetricEnum queryMetricEnum = QueryMetricEnum.getByType((String) query.get("queryMetricType"));
				if (queryMetricEnum == QueryMetricEnum.COUNT) {
					bucketOrder = BucketOrder.count(("asc".equals(queryOrder)) ? true : false);
				}
				else {
					bucketOrder = BucketOrder.aggregation("aggsByMetric", ("asc".equals(queryOrder)) ? true : false);
				}
				break;
			}
			case "terms": {
				bucketOrder = BucketOrder.key(("asc".equals(queryOrder)) ? true : false);
				break;
			}
			default: {
				bucketOrder = BucketOrder.count(true);
				break;
			}
		}
		return bucketOrder;
	}

	private SearchSourceBuilder getSearchRequest(ZonedDateTime gte, ZonedDateTime lte, String queryString) {
		String queryStringStr = (!StringUtils.hasText(queryString)) ? "*" : queryString;
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.size(1);
		searchSourceBuilder.query(QueryBuilders
			.boolQuery()
				.filter(QueryBuilders.rangeQuery("@timestamp")
					.gte(gte.toInstant().toEpochMilli())
					.lte(lte.toInstant().toEpochMilli())
					.format("epoch_millis"))
				.filter(QueryBuilders.queryStringQuery(queryStringStr).analyzeWildcard(true)));
		
		return searchSourceBuilder;
	}

	private SearchResponse getSearchResponse(String queryIndexAlias, SearchSourceBuilder searchSourceBuilder) throws Exception {
		SearchRequest searchRequest = new SearchRequest(queryIndexAlias);
		searchRequest.source(searchSourceBuilder);
		searchRequest.scroll(TimeValue.timeValueSeconds(10));
		return elasticsearchFactory.searchIndex(searchRequest);
	}
	
}