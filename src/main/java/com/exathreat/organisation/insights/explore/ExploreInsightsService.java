package com.exathreat.organisation.insights.explore;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import com.exathreat.common.config.factory.ElasticsearchFactory;
import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationDetection;
import com.exathreat.common.jpa.entity.OrganisationQuery;
import com.exathreat.common.jpa.entity.OrganisationUser;
import com.exathreat.common.jpa.repository.OrganisationDetectionRepository;
import com.exathreat.common.jpa.repository.OrganisationIndexRepository;
import com.exathreat.common.jpa.repository.OrganisationQueryRepository;
import com.exathreat.common.service.TimeSeriesSearchService;
import com.exathreat.common.support.CollectionSupport;
import com.exathreat.common.support.HackSupport;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ExtendedBounds;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram.ParsedBucket;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class ExploreInsightsService {

	@Autowired
	private CollectionSupport collectionSupport;

	@Autowired
	private ElasticsearchFactory elasticsearchFactory;

	@Autowired
	private HackSupport hackSupport;

	@Autowired
	private OrganisationDetectionRepository organisationDetectionRepository;

	@Autowired
	private OrganisationIndexRepository organisationIndexRepository;

	@Autowired
	private OrganisationQueryRepository organisationQueryRepository;

	@Autowired
	private TimeSeriesSearchService timeSeriesSearchService;

	public void getInsightsMetadata(ModelMap modelMap) throws Exception {
		modelMap.addAttribute("indices", organisationIndexRepository.findByOrganisationOrderByAliasNameDescCreatedDesc((Organisation) modelMap.get("currentOrganisation")));
	}

	@Transactional(readOnly = false)
	public Map<String, Object> listExploreItems(String orgCode, ModelMap modelMap) throws Exception {
		List<OrganisationQuery> organisationQueries = organisationQueryRepository.findByOrganisationAndEnabledOrderByNameAsc((Organisation) modelMap.get("currentOrganisation"), true);
		List<OrganisationDetection> organisationDetections = organisationDetectionRepository.findByOrganisationOrderByNameAscSeverityDescVersionDesc((Organisation) modelMap.get("currentOrganisation"));
		
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

		if (!organisationQueries.isEmpty()) {
			List<Map<String, Object>> queryValues = new ArrayList<>();
			for (OrganisationQuery organisationQuery : organisationQueries) {
				queryValues.add(Map.of("name", organisationQuery.getName(), "value", "query:" + organisationQuery.getQueryCode(), "icon", "file code"));
			}
			results.add(Map.of("name", "Queries", "type", "menu", "values", queryValues));
		}

		if (!organisationDetections.isEmpty()) {
			List<Map<String, Object>> detectionValues = new ArrayList<>();
			for (OrganisationDetection organisationDetection : organisationDetections) {
				detectionValues.add(Map.of("name", organisationDetection.getName(), "value", "detection:" + organisationDetection.getDetCode(), "icon", "bullseye"));
			}
			results.add(Map.of("name", "Detections", "type", "menu", "values", detectionValues));
		}

		Map<String, Object> response = new HashMap<String, Object>();
		response.put("success", true);
		response.put("results", results);
		return response;
	}

	@Transactional(readOnly = true)
	public ExploreInsightItemData openExploreItem(String orgCode, ExploreInsightItemData exploreInsightItemData, ModelMap modelMap) throws Exception {
		switch (exploreInsightItemData.getItemType()) {
			case "query":
				exploreInsightItemData.setOrganisationQuery(organisationQueryRepository.findByQueryCode(exploreInsightItemData.getOrganisationQuery().getQueryCode()));
				break;
			case "detection":
				exploreInsightItemData.setOrganisationDetection(organisationDetectionRepository.findByDetCode(exploreInsightItemData.getOrganisationDetection().getDetCode()));
				break;
			default:
				break;
		}
		return exploreInsightItemData;
	}

	@Transactional(readOnly = false)
	public ExploreInsightItemData saveExploreItem(String orgCode, ExploreInsightItemData exploreInsightItemData, ModelMap modelMap) throws Exception {
		switch (exploreInsightItemData.getItemType()) {
			case "query":
				OrganisationQuery organisationQueryDto = exploreInsightItemData.getOrganisationQuery();
				organisationQueryDto.setModified(ZonedDateTime.now(ZoneOffset.UTC));

				OrganisationQuery organisationQuery = OrganisationQuery.builder().build();
				if ("new".equals(exploreInsightItemData.getSaveAction())) {
					organisationQueryDto.setQueryCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12));
					organisationQueryDto.setEnabled(true);
					organisationQueryDto.setCreated(organisationQueryDto.getModified());
					organisationQueryDto.setOrganisation((Organisation) modelMap.get("currentOrganisation"));
					organisationQueryDto.setOrganisationUser((OrganisationUser) modelMap.get("loggedInUser"));
					BeanUtils.copyProperties(organisationQueryDto, organisationQuery);
				}
				else if ("update".equals(exploreInsightItemData.getSaveAction())) {
					organisationQuery = organisationQueryRepository.findByQueryCode(organisationQueryDto.getQueryCode());
					BeanUtils.copyProperties(organisationQueryDto, organisationQuery, "id", "queryCode", "enabled", "created", "organisation", "organisationUser");
				}
				exploreInsightItemData.setOrganisationQuery(organisationQueryRepository.saveAndFlush(organisationQuery));
				break;
			case "detection":
				OrganisationDetection organisationDetectionDto = exploreInsightItemData.getOrganisationDetection();
				organisationDetectionDto.setModified(ZonedDateTime.now(ZoneOffset.UTC));

				OrganisationDetection organisationDetection = OrganisationDetection.builder().build();
				if ("new".equals(exploreInsightItemData.getSaveAction())) {
					organisationDetectionDto.setDetCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12));
					organisationDetectionDto.setSeverity(1);
					organisationDetectionDto.setRiskScore(50);
					organisationDetectionDto.setRuntime(5);
					organisationDetectionDto.setRuntimeUnit("minute");
					organisationDetectionDto.setVersion(1);
					organisationDetectionDto.setEnabled(false);
					organisationDetectionDto.setCreated(organisationDetectionDto.getModified());
					organisationDetectionDto.setOrganisation((Organisation) modelMap.get("currentOrganisation"));
					organisationDetectionDto.setOrganisationUser((OrganisationUser) modelMap.get("loggedInUser"));
					BeanUtils.copyProperties(organisationDetectionDto, organisationDetection);
				}
				else if ("update".equals(exploreInsightItemData.getSaveAction())) {
					organisationDetection = organisationDetectionRepository.findByDetCode(organisationDetectionDto.getDetCode());
					BeanUtils.copyProperties(organisationDetectionDto, organisationDetection, "id", "detCode", "severity", "riskScore", "runtime", "runtimeUnit", "version", "enabled", "created", "organisation", "organisationUser");
				}
				exploreInsightItemData.setOrganisationDetection(organisationDetectionRepository.saveAndFlush(organisationDetection));
				break;
			default:
				break;
		}
		return exploreInsightItemData;
	}
	
	public boolean loadExploreData(String orgCode, ExploreInsightsData exploreInsightsData, ModelMap modelMap) throws Exception {
		boolean loaded = false;
		try {
			elasticsearchFactory.clearScroll(exploreInsightsData.getScrollId());

			Map<String, Object> dateRangeInfo = timeSeriesSearchService.getDateRangeInfo(
				exploreInsightsData.getDateRange(), 
				exploreInsightsData.getDateRangeFormat(), 
				exploreInsightsData.getIntervalUnit(), 
				modelMap);

			if (dateRangeInfo.isEmpty()) {
				modelMap.addAttribute("error", Map.of("type", "Date range exceeded", "reason", "The date range is out of bounds."));
			}
			else {
				SearchResponse searchResponse = elasticsearchFactory.searchIndex(buildExploreSearchRequest(exploreInsightsData, dateRangeInfo));
				if (searchResponse.getHits().getTotalHits().value == 0) {
					modelMap.addAttribute("error", Map.of("type", "Empty results", "reason", "No results match your search criteria"));
				}
				else {
					exploreInsightsData.setDateRangeInfo(dateRangeInfo);
					exploreInsightsData.setSearchResults(parseExploreSearchResponse(searchResponse, modelMap));

					modelMap.addAttribute("exploreInsightsData", exploreInsightsData);
					loaded = true;
				}
			}
		}
		catch (ElasticsearchException exception) {
			modelMap.addAttribute("error", Map.of("type", "Failed to return results", "reason", exception.getMessage()));
		}
		catch (Exception exception) {
			modelMap.addAttribute("error", Map.of("type", "Failed to return results", "reason", exception.getMessage()));
		}
		return loaded;
	}

	public boolean scrollExploreData(String orgCode, ExploreInsightsData exploreInsightsData, ModelMap modelMap) throws Exception {
		boolean scrolled = false;
		try {
			SearchResponse searchResponse = elasticsearchFactory.searchScroll(exploreInsightsData.getScrollId());
			if (searchResponse.getHits().getHits().length == 0) {
				elasticsearchFactory.clearScroll(exploreInsightsData.getScrollId());
			}
			else {
				exploreInsightsData.setSearchResults(parseExploreSearchResponse(searchResponse, modelMap));

				modelMap.addAttribute("exploreInsightsData", exploreInsightsData);
				scrolled = true;
			}
		}
		catch (ElasticsearchException exception) {}
		catch (Exception exception) {}
		return scrolled;
	}

	@SuppressWarnings("unchecked")
	private SearchRequest buildExploreSearchRequest(ExploreInsightsData exploreInsightsData, Map<String, Object> dateRangeInfo) throws Exception {
		ZonedDateTime gte = (ZonedDateTime) dateRangeInfo.get("gte");
		ZonedDateTime lte = (ZonedDateTime) dateRangeInfo.get("lte");
		Map<String, Object> timeInterval = (Map<String, Object>) dateRangeInfo.get("timeInterval");

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.size(50);
		searchSourceBuilder.query(QueryBuilders
			.boolQuery()
				.filter(QueryBuilders.rangeQuery("@timestamp")
					.gte(gte.toInstant().toEpochMilli())
					.lte(lte.toInstant().toEpochMilli())
					.format("epoch_millis"))
				.filter(QueryBuilders.queryStringQuery(exploreInsightsData.getQueryStr()).analyzeWildcard(true)));
		
		searchSourceBuilder.aggregation(AggregationBuilders.dateHistogram("search")
			.field("@timestamp")
			.fixedInterval((DateHistogramInterval) timeInterval.get("interval"))
			.minDocCount(exploreInsightsData.getMinDocCount())
			.extendedBounds(new ExtendedBounds(gte.toInstant().toEpochMilli(), lte.toInstant().toEpochMilli())));

		searchSourceBuilder.highlighter(new HighlightBuilder().field("*", 100));		
		searchSourceBuilder.sort("@timestamp", SortOrder.DESC);

		SearchRequest searchRequest = new SearchRequest(exploreInsightsData.getIndexAlias());
		searchRequest.source(searchSourceBuilder);
		searchRequest.scroll(TimeValue.timeValueMinutes(1));
		return searchRequest;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> parseExploreSearchResponse(SearchResponse searchResponse, ModelMap modelMap) {
		List<Map<String, Object>> series = new ArrayList<Map<String, Object>>();

		if (searchResponse.getAggregations() != null) {

			// date histogram aggregation...

			ParsedDateHistogram parsedDateHistogram = (ParsedDateHistogram) searchResponse.getAggregations().getAsMap().get("search");

			List<List<Object>> searchBuckets = new ArrayList<List<Object>>();
			for (ParsedBucket parsedBucket : (List<ParsedBucket>) parsedDateHistogram.getBuckets()) {
				OffsetDateTime utctz = hackSupport.hackUTCOnZoneDateTime((ZonedDateTime) parsedBucket.getKey(), (String) modelMap.get("timezone"));
				
				List<Object> searchBucket = List.of(utctz.toInstant().toEpochMilli(), parsedBucket.getDocCount());
				searchBuckets.add(searchBucket);
			}
			series = List.of(Map.of("name", "data", "data", searchBuckets));
		}

		// search result documents...

		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

		SearchHit[] searchHits = searchResponse.getHits().getHits();
		for (SearchHit searchHit : searchHits) {
			TreeMap<String, Object> result = new TreeMap<String, Object>();
			result.put("_id", searchHit.getId());
			collectionSupport.flattenSearchHitMap("", searchHit.getSourceAsMap(), result);

			if (!searchHit.getHighlightFields().isEmpty()) {
				Map<String, HighlightField> highlightedFields = searchHit.getHighlightFields();
				for (Map.Entry<String, HighlightField> highlightEntry : highlightedFields.entrySet()) {
					HighlightField highlightField = highlightEntry.getValue();
					if (result.containsKey(highlightField.getName())) {
						String resultField = result.get(highlightField.getName()).toString();
						for (Text text : highlightField.getFragments()) {
							resultField = resultField.replace(text.string().replaceAll("<\\D?em>", ""), text.string());
						}
						result.put(highlightField.getName(), resultField);
					}
				}
			}
			results.add(result);
		}

		return Map.of(
			"scrollId", searchResponse.getScrollId(), 
			"series", series, 
			"tookInMs", searchResponse.getTook().getMillis(), 
			"resultSizeTotal", searchResponse.getHits().getTotalHits().value, 
			"resultSize", searchResponse.getHits().getHits().length, 
			"results", results, 
			"batchSize", 50);
	}
}