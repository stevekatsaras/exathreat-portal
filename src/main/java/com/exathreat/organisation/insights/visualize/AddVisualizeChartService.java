package com.exathreat.organisation.insights.visualize;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import com.exathreat.common.config.factory.ElasticsearchFactory;
import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationIndex;
import com.exathreat.common.jpa.entity.OrganisationVisualization;
import com.exathreat.common.jpa.repository.OrganisationIndexRepository;
import com.exathreat.common.jpa.repository.OrganisationVisualizationRepository;
import com.exathreat.common.service.TimeSeriesSearchService;
import com.exathreat.common.support.CollectionSupport;
import com.exathreat.common.support.HackSupport;
import com.exathreat.common.support.JsonSupport;
import com.exathreat.common.support.TimestampSupport;
import com.exathreat.organisation.insights.visualize.enums.QueryAggregateEnum;
import com.exathreat.organisation.insights.visualize.enums.QueryMetricEnum;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetadata;
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
public class AddVisualizeChartService {

	@Autowired
	private CollectionSupport collectionSupport;

	@Autowired
	private ElasticsearchFactory elasticsearchFactory;

	@Autowired
	private HackSupport hackSupport;

	@Autowired
	private JsonSupport jsonSupport;

	@Autowired
	private OrganisationIndexRepository organisationIndexRepository;

	@Autowired
	private OrganisationVisualizationRepository organisationVisualizationRepository;

	@Autowired
	private TimeSeriesSearchService timeSeriesSearchService;

	@Autowired
	private TimestampSupport timestampSupport;

	public void getChartMetadata(ModelMap modelMap) throws Exception {
		modelMap.addAttribute("indices", organisationIndexRepository.findByOrganisationOrderByAliasNameDescCreatedDesc((Organisation) modelMap.get("currentOrganisation")));
		modelMap.addAttribute("queryMetricTypes", Arrays.asList(QueryMetricEnum.values()));
		modelMap.addAttribute("queryAggregateTypes", Arrays.asList(QueryAggregateEnum.values()));
	}
	
	@Transactional(readOnly = true)
	public void initInsightsAddChartVisualization(String orgCode, String vizCode, AddVisualizeChartForm addVisualizeChartForm, ModelMap modelMap) throws Exception {		
		addVisualizeChartForm.setOrganisationVisualization(organisationVisualizationRepository.findByVizCode(vizCode));
		addVisualizeChartForm.setChartType("line");
		addVisualizeChartForm.setChartBackground("#fff");
		addVisualizeChartForm.setChartForeground("#373d3f");
		addVisualizeChartForm.setChartThemeMode("light");
		addVisualizeChartForm.setChartPalette("palette1");
		addVisualizeChartForm.setChartTitle("Untitled");
		addVisualizeChartForm.setChartTitleAlign("left");
		addVisualizeChartForm.setChartTitleMargin(10);
		addVisualizeChartForm.setChartTitleOffsetX(0);
		addVisualizeChartForm.setChartTitleOffsetY(0);
		addVisualizeChartForm.setChartTitleColor("#263238");
		addVisualizeChartForm.setChartGridShow(true);
		addVisualizeChartForm.setChartGridColor("#90A4AE");
		addVisualizeChartForm.setChartGridShowX(false);
		addVisualizeChartForm.setChartGridShowY(true);
		addVisualizeChartForm.setChartGridPosition("back");
		addVisualizeChartForm.setChartDataLabelsShow(false);
		addVisualizeChartForm.setChartLegendShow(true);
		addVisualizeChartForm.setChartLegendAlign("center");
		addVisualizeChartForm.setChartLegendPosition("bottom");
		addVisualizeChartForm.setChartLegendOffsetX(0);
		addVisualizeChartForm.setChartLegendOffsetY(0);
		addVisualizeChartForm.setChartLegendIconWidth(12);
		addVisualizeChartForm.setChartLegendIconHeight(12);
		addVisualizeChartForm.setChartLegendIconRadius(12);
		addVisualizeChartForm.setChartLegendIconOffsetX(0);
		addVisualizeChartForm.setChartLegendIconOffsetY(0);
		addVisualizeChartForm.setChartMarkerSize(0);
		addVisualizeChartForm.setChartMarkerShape("circle");
		addVisualizeChartForm.setChartMarkerRadius(2);
		addVisualizeChartForm.setChartMarkerOffsetX(0);
		addVisualizeChartForm.setChartMarkerOffsetY(0);
		addVisualizeChartForm.setChartStrokeShow(true);
		addVisualizeChartForm.setChartStrokeCurve("smooth");
		addVisualizeChartForm.setChartStrokeWidth(5);
		addVisualizeChartForm.setChartTooltipEnabled(true);
		addVisualizeChartForm.setChartXAxisShow(true);
		addVisualizeChartForm.setChartXAxisType("datetime");
		addVisualizeChartForm.setChartXAxisTitle("");
		addVisualizeChartForm.setChartXAxisTitleOffsetX(0);
		addVisualizeChartForm.setChartXAxisTitleOffsetY(0);
		addVisualizeChartForm.setChartYAxisShow(true);
		addVisualizeChartForm.setChartYAxisOpposite(false);
		addVisualizeChartForm.setChartYAxisFlip(false);
		addVisualizeChartForm.setChartYAxisDecimals(0);
		addVisualizeChartForm.setChartYAxisTitle("");
		addVisualizeChartForm.setChartYAxisTitleOffsetX(0);
		addVisualizeChartForm.setChartYAxisTitleOffsetY(0);
		addVisualizeChartForm.setQueries(new ArrayList<Map<String, Object>>());
		addQuery(orgCode, vizCode, addVisualizeChartForm, modelMap);
	}

	@Transactional(readOnly = false)
	@SuppressWarnings("unchecked")
	public void doInsightsAddChartVisualization(String orgCode, String vizCode, AddVisualizeChartForm addVisualizeChartForm, ModelMap modelMap) throws Exception {
		OrganisationVisualization organisationVisualizationChartDto = addVisualizeChartForm.getOrganisationVisualization();

		Map<String, Object> panelMap = jsonSupport.toHashMap(addVisualizeChartForm);
		panelMap.put("chartCode", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12));
		panelMap.remove("organisationVisualization");
		panelMap.remove("dateRangeInfo");
		panelMap.remove("searchResults");
		
		OrganisationVisualization organisationVisualization = organisationVisualizationRepository.findByVizCode(vizCode);
		((List<Map<String, Object>>) organisationVisualization.getCharts().get("panels")).add(panelMap);

		organisationVisualization.setDateRange(organisationVisualizationChartDto.getDateRange());
		organisationVisualization.setDateFormat(organisationVisualizationChartDto.getDateFormat());
		organisationVisualization.setTimeFormat(organisationVisualizationChartDto.getTimeFormat());
		organisationVisualization.setModified(ZonedDateTime.now(ZoneOffset.UTC));
		organisationVisualizationRepository.saveAndFlush(organisationVisualization);
	}

	@SuppressWarnings("unchecked")
	public void addQuery(String orgCode, String vizCode, AddVisualizeChartForm addVisualizeChartForm, ModelMap modelMap) throws Exception {
		OrganisationIndex organisationIndex = ((List<OrganisationIndex>) modelMap.get("indices")).get(0);

		Map<String, Object> query = Map.ofEntries(
			Map.entry("queryShow", true),
			Map.entry("queryIndexAlias", organisationIndex.getAliasName()),
			Map.entry("queryName", ""),
			Map.entry("queryStr", ""),
			Map.entry("queryMetricType", QueryMetricEnum.COUNT.getType()),
			Map.entry("queryMetricField", ""),
			Map.entry("queryAggregateType", QueryAggregateEnum.DATEHISTOGRAM.getType()),
			Map.entry("queryAggregateField", "@timestamp"),
			Map.entry("queryMinDocCount", 0),
			Map.entry("queryLimit", 10),
			Map.entry("queryOrder", "asc"),
			Map.entry("queryOrderBy", "metric")
		);

		if (addVisualizeChartForm.getQueries() == null) {
			addVisualizeChartForm.setQueries(new ArrayList<Map<String, Object>>());
		}
		addVisualizeChartForm.getQueries().add(query);
	}

	public void cloneQuery(String orgCode, String vizCode, AddVisualizeChartForm addVisualizeChartForm, int queryIndex, ModelMap modelMap) throws Exception {
		addVisualizeChartForm.getQueries().add(addVisualizeChartForm.getQueries().get(queryIndex));
	}

	public void removeQuery(String orgCode, String vizCode, AddVisualizeChartForm addVisualizeChartForm, int queryIndex, ModelMap modelMap) throws Exception {
		addVisualizeChartForm.getQueries().remove(queryIndex);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getFieldMappings(String orgCode, String vizCode, Map<String, Object> params, ModelMap modelMap) throws Exception {
		GetMappingsResponse getMappingsResponse = elasticsearchFactory.getFieldMappings((String) params.get("queryIndexAlias"));
		Map<String, MappingMetadata> allMappings = getMappingsResponse.mappings();

		String queryFieldType = (String) params.get("queryFieldType");
		QueryMetricEnum queryMetricEnum = QueryMetricEnum.getByType(queryFieldType);
		QueryAggregateEnum queryAggregateEnum = QueryAggregateEnum.getByType(queryFieldType);
		
		String dataFieldType = (queryMetricEnum != null) ? queryMetricEnum.getDataType() : ((queryAggregateEnum != null) ? queryAggregateEnum.getDataType() : "text");
		List<Map<String, Object>> values = new ArrayList<Map<String, Object>>();

		TreeMap<String, Object> flattenedFieldProperties = new TreeMap<String, Object>();
		Map<String, Object> fieldProperties = (Map<String, Object>) allMappings.entrySet().iterator().next().getValue().getSourceAsMap().get("properties");
		collectionSupport.flattenFieldMappingMap("", fieldProperties, flattenedFieldProperties);

		for (Map.Entry<String,Object> fieldProperty : flattenedFieldProperties.entrySet()) {
			if ("text".equals(dataFieldType)) {
				if (dataFieldType.equals(fieldProperty.getValue())) {
					values.add(new HashMap<String, Object>(Map.of("name", fieldProperty.getKey() + ".keyword", "value", fieldProperty.getKey() + ".keyword")));
				}
				else {
					values.add(new HashMap<String, Object>(Map.of("name", fieldProperty.getKey(), "value", fieldProperty.getKey())));
				}
			}
			else if (dataFieldType.equals(fieldProperty.getValue())) {
				values.add(new HashMap<String, Object>(Map.of("name", fieldProperty.getKey(), "value", fieldProperty.getKey())));
			}
		}
		return Map.of("values", values);
	}

	public AddVisualizeChartForm loadData(String orgCode, String vizCode, AddVisualizeChartForm addVisualizeChartForm, ModelMap modelMap) throws Exception {
		try {
			OrganisationVisualization organisationVisualization = addVisualizeChartForm.getOrganisationVisualization();

			Map<String, Object> dateRangeInfo = timeSeriesSearchService.getDateRangeInfo(
				addVisualizeChartForm.getOrganisationVisualization().getDateRange(), 
				organisationVisualization.getDateFormat() + " " + organisationVisualization.getTimeFormat(), 
				"auto", 
				modelMap);

			if (!dateRangeInfo.isEmpty()) {
				addVisualizeChartForm.setDateRangeInfo(dateRangeInfo);

				switch (addVisualizeChartForm.getChartType()) {
					case "area":
						addVisualizeChartForm.setSearchResults(runQueriesForLinearCharts(addVisualizeChartForm, dateRangeInfo, modelMap));
						break;
					case "bar":
						addVisualizeChartForm.setSearchResults(runQueriesForLinearCharts(addVisualizeChartForm, dateRangeInfo, modelMap));
						break;
					case "line":
						addVisualizeChartForm.setSearchResults(runQueriesForLinearCharts(addVisualizeChartForm, dateRangeInfo, modelMap));
						break;
					case "treemap":
						addVisualizeChartForm.setSearchResults(runQueriesForLinearCharts(addVisualizeChartForm, dateRangeInfo, modelMap));
						break;
					case "pie":
						addVisualizeChartForm.setSearchResults(runQueryForCircularCharts(addVisualizeChartForm, dateRangeInfo, modelMap));
						break;
					case "donut":
						addVisualizeChartForm.setSearchResults(runQueryForCircularCharts(addVisualizeChartForm, dateRangeInfo, modelMap));
						break;
					case "polarArea":
						addVisualizeChartForm.setSearchResults(runQueryForCircularCharts(addVisualizeChartForm, dateRangeInfo, modelMap));
						break;
					default:
						break;
				}
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		return addVisualizeChartForm;
	}

	/**
	 * runs any queries that are for 'linear' charts
	 * - area, bar, column, line
	 */
	private Map<String, Object> runQueriesForLinearCharts(AddVisualizeChartForm addVisualizeChartForm, Map<String, Object> dateRangeInfo, ModelMap modelMap) throws Exception {
		List<Map<String, Object>> series = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> query : addVisualizeChartForm.getQueries()) {
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
	private Map<String, Object> runQueryForCircularCharts(AddVisualizeChartForm addVisualizeChartForm, Map<String, Object> dateRangeInfo, ModelMap modelMap) throws Exception {
		Map<String, Object> results = null;
		for(Map<String, Object> query : addVisualizeChartForm.getQueries()) {
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