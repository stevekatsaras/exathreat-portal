package com.exathreat.organisation.forensics.threats;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.exathreat.common.config.factory.ElasticsearchFactory;
import com.exathreat.common.service.TimeSeriesSearchService;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
public class ThreatsForensicsService {

	@Autowired
	private ElasticsearchFactory elasticsearchFactory;

	@Autowired
	private TimeSeriesSearchService timeSeriesSearchService;
	
	public ThreatsForensicsPanelData loadData(String orgCode, String action, ThreatsForensicsPanelData threatsForensicsPanelData, ModelMap modelMap) throws Exception {
		try {
			String esAlias = "exathreat-intel";
			Map<String, Object> dateRangeInfo = timeSeriesSearchService.getDateRangeInfo(
				threatsForensicsPanelData.getDateRange(), 
				threatsForensicsPanelData.getDateRangeFormat(), 
				"auto", 
				modelMap);

			if (!dateRangeInfo.isEmpty()) {
				threatsForensicsPanelData.setDateRangeInfo(dateRangeInfo);

				if ("bytype".equals(action)) {
					threatsForensicsPanelData.setSearchResults(searchRequestByType(esAlias, dateRangeInfo, modelMap));
				}
				else if ("bygeo".equals(action)) {
					threatsForensicsPanelData.setSearchResults(searchRequestByGeo(esAlias, dateRangeInfo, modelMap));
				}
				else if ("byprovider".equals(action)) {
					threatsForensicsPanelData.setSearchResults(searchRequestByProvider(esAlias, dateRangeInfo, modelMap));
				}
				else if ("byconfidence".equals(action)) {
					threatsForensicsPanelData.setSearchResults(searchRequestByConfidence(esAlias, dateRangeInfo, modelMap));
				}
				else if ("bycountry".equals(action)) {
					threatsForensicsPanelData.setSearchResults(searchRequestByCountry(esAlias, dateRangeInfo, modelMap));
				}
				else if ("byasn".equals(action)) {
					threatsForensicsPanelData.setSearchResults(searchRequestByAsn(esAlias, dateRangeInfo, modelMap));
				}
			}
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		return threatsForensicsPanelData;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> searchRequestByType(String esAlias, Map<String, Object> dateRangeInfo, ModelMap modelMap) throws Exception {
		SearchSourceBuilder searchSourceBuilder = getSearchRequest(dateRangeInfo);
		searchSourceBuilder.aggregation(AggregationBuilders.terms("type")
			.field("type.keyword")
			.size(5)
			.order(BucketOrder.count(false)));

		SearchRequest searchRequest = new SearchRequest(esAlias);
		searchRequest.source(searchSourceBuilder);
		searchRequest.scroll(TimeValue.timeValueSeconds(10));

		SearchResponse searchResponse = elasticsearchFactory.searchIndex(searchRequest);

		List<Object> labels = new ArrayList<Object>();
		List<Long> series = new ArrayList<Long>();

		// term aggregation

		ParsedStringTerms parsedStringTerms = (ParsedStringTerms) searchResponse.getAggregations().asMap().get("type");
		for (ParsedStringTerms.ParsedBucket parsedStringTermBucket : (List<ParsedStringTerms.ParsedBucket>) parsedStringTerms.getBuckets()) {
			labels.add(parsedStringTermBucket.getKey());
			series.add(parsedStringTermBucket.getDocCount());
		}

		return Map.of(
			"labels", labels,
			"series", series, 
			"tookInMs", searchResponse.getTook().getMillis(), 
			"resultsSizeTotal", searchResponse.getHits().getTotalHits().value);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> searchRequestByGeo(String esAlias, Map<String, Object> dateRangeInfo, ModelMap modelMap) throws Exception {
		SearchSourceBuilder searchSourceBuilder = getSearchRequest(dateRangeInfo);
		searchSourceBuilder.aggregation(AggregationBuilders.terms("ip")
			.field("ipv4.keyword")
			.size(100)
			.order(BucketOrder.count(false))
			.subAggregation(AggregationBuilders.topHits("latest")
				.fetchSource(new String[] {
					"geo_city.country.name",
					"geo_city.location.lat", 
					"geo_city.location.lon",
					"geo_city.name",
					"geo_city.postcode"
				}, null)
				.sort("@timestamp", SortOrder.DESC)
				.size(1)));

		SearchRequest searchRequest = new SearchRequest(esAlias);
		searchRequest.source(searchSourceBuilder);
		searchRequest.scroll(TimeValue.timeValueSeconds(10));

		SearchResponse searchResponse = elasticsearchFactory.searchIndex(searchRequest);

		List<Object> geoIps = new ArrayList<Object>();

		// term aggregation

		ParsedStringTerms parsedStringTerms = (ParsedStringTerms) searchResponse.getAggregations().asMap().get("ip");
		for (ParsedStringTerms.ParsedBucket parsedStringTermBucket : (List<ParsedStringTerms.ParsedBucket>) parsedStringTerms.getBuckets()) {
				ParsedTopHits parsedTopHits = (ParsedTopHits) parsedStringTermBucket.getAggregations().asMap().get("latest");
				Map<String, Object> sourceMap = parsedTopHits.getHits().getHits()[0].getSourceAsMap();

				geoIps.add(Map.of(
					"ipv4", parsedStringTermBucket.getKey(), 
					"city", sourceMap.getOrDefault("geo_city.name", ""), 
					"postcode", sourceMap.getOrDefault("geo_city.postcode", ""), 
					"country", sourceMap.getOrDefault("geo_city.country.name", ""), 
					"lat", sourceMap.getOrDefault("geo_city.location.lat", ""), 
					"lon", sourceMap.getOrDefault("geo_city.location.lon", ""))
				);
		}

		return Map.of(
			"geoIps", geoIps, 
			"tookInMs", searchResponse.getTook().getMillis(), 
			"resultsSizeTotal", searchResponse.getHits().getTotalHits().value);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> searchRequestByProvider(String esAlias, Map<String, Object> dateRangeInfo, ModelMap modelMap) throws Exception {
		SearchSourceBuilder searchSourceBuilder = getSearchRequest(dateRangeInfo);
		searchSourceBuilder.aggregation(AggregationBuilders.terms("provider")
			.field("provider.keyword")
			.size(5)
			.order(BucketOrder.count(false)));

		SearchRequest searchRequest = new SearchRequest(esAlias);
		searchRequest.source(searchSourceBuilder);
		searchRequest.scroll(TimeValue.timeValueSeconds(10));

		SearchResponse searchResponse = elasticsearchFactory.searchIndex(searchRequest);

		List<Object> labels = new ArrayList<Object>();
		List<Long> series = new ArrayList<Long>();

		// term aggregation

		ParsedStringTerms parsedStringTerms = (ParsedStringTerms) searchResponse.getAggregations().asMap().get("provider");
		for (ParsedStringTerms.ParsedBucket parsedStringTermBucket : (List<ParsedStringTerms.ParsedBucket>) parsedStringTerms.getBuckets()) {
			labels.add(parsedStringTermBucket.getKey());
			series.add(parsedStringTermBucket.getDocCount());
		}

		return Map.of(
			"labels", labels,
			"series", series, 
			"tookInMs", searchResponse.getTook().getMillis(), 
			"resultsSizeTotal", searchResponse.getHits().getTotalHits().value);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> searchRequestByConfidence(String esAlias, Map<String, Object> dateRangeInfo, ModelMap modelMap) throws Exception {
		SearchSourceBuilder searchSourceBuilder = getSearchRequest(dateRangeInfo);
		searchSourceBuilder.aggregation(AggregationBuilders.terms("confidence")
			.field("confidence")
			.size(5)
			.order(BucketOrder.count(false)));

		SearchRequest searchRequest = new SearchRequest(esAlias);
		searchRequest.source(searchSourceBuilder);
		searchRequest.scroll(TimeValue.timeValueSeconds(10));

		SearchResponse searchResponse = elasticsearchFactory.searchIndex(searchRequest);

		List<Object> labels = new ArrayList<Object>();
		List<Long> series = new ArrayList<Long>();

		// term aggregation

		ParsedLongTerms parsedLongTerms = (ParsedLongTerms) searchResponse.getAggregations().asMap().get("confidence");
		for (ParsedLongTerms.ParsedBucket parsedLongTermBucket : (List<ParsedLongTerms.ParsedBucket>) parsedLongTerms.getBuckets()) {
			labels.add(parsedLongTermBucket.getKey().toString());
			series.add(parsedLongTermBucket.getDocCount());
		}

		return Map.of(
			"labels", labels,
			"series", series, 
			"tookInMs", searchResponse.getTook().getMillis(), 
			"resultsSizeTotal", searchResponse.getHits().getTotalHits().value);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> searchRequestByCountry(String esAlias, Map<String, Object> dateRangeInfo, ModelMap modelMap) throws Exception {
		SearchSourceBuilder searchSourceBuilder = getSearchRequest(dateRangeInfo);
		searchSourceBuilder.aggregation(AggregationBuilders.terms("country")
			.field("geo_city.country.name.keyword")
			.size(5)
			.order(BucketOrder.count(false)));

		SearchRequest searchRequest = new SearchRequest(esAlias);
		searchRequest.source(searchSourceBuilder);
		searchRequest.scroll(TimeValue.timeValueSeconds(10));

		SearchResponse searchResponse = elasticsearchFactory.searchIndex(searchRequest);

		List<Map<String, Object>> buckets = new ArrayList<Map<String, Object>>();

		// term aggregation

		ParsedStringTerms parsedStringTerms = (ParsedStringTerms) searchResponse.getAggregations().asMap().get("country");
		for (ParsedStringTerms.ParsedBucket parsedStringTermBucket : (List<ParsedStringTerms.ParsedBucket>) parsedStringTerms.getBuckets()) {
			buckets.add(Map.of("x", parsedStringTermBucket.getKey(), "y", parsedStringTermBucket.getDocCount()));
		}

		List<Map<String, Object>> series = List.of(Map.of("name", "Countries", "data", buckets));

		return Map.of(
			"series", series, 
			"tookInMs", searchResponse.getTook().getMillis(), 
			"resultsSizeTotal", searchResponse.getHits().getTotalHits().value);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> searchRequestByAsn(String esAlias, Map<String, Object> dateRangeInfo, ModelMap modelMap) throws Exception {
		SearchSourceBuilder searchSourceBuilder = getSearchRequest(dateRangeInfo);
		searchSourceBuilder.aggregation(AggregationBuilders.terms("asn")
			.field("geo_asn.organization.keyword")
			.size(5)
			.order(BucketOrder.count(false)));

		SearchRequest searchRequest = new SearchRequest(esAlias);
		searchRequest.source(searchSourceBuilder);
		searchRequest.scroll(TimeValue.timeValueSeconds(10));

		SearchResponse searchResponse = elasticsearchFactory.searchIndex(searchRequest);

		List<Map<String, Object>> buckets = new ArrayList<Map<String, Object>>();

		// term aggregation

		ParsedStringTerms parsedStringTerms = (ParsedStringTerms) searchResponse.getAggregations().asMap().get("asn");
		for (ParsedStringTerms.ParsedBucket parsedStringTermBucket : (List<ParsedStringTerms.ParsedBucket>) parsedStringTerms.getBuckets()) {
			buckets.add(Map.of("x", parsedStringTermBucket.getKey(), "y", parsedStringTermBucket.getDocCount()));
		}

		List<Map<String, Object>> series = List.of(Map.of("name", "ASNs", "data", buckets));

		return Map.of(
			"series", series, 
			"tookInMs", searchResponse.getTook().getMillis(), 
			"resultsSizeTotal", searchResponse.getHits().getTotalHits().value);
	}

	private SearchSourceBuilder getSearchRequest(Map<String, Object> dateRangeInfo) {
		ZonedDateTime gte = (ZonedDateTime) dateRangeInfo.get("gte");
		ZonedDateTime lte = (ZonedDateTime) dateRangeInfo.get("lte");
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.size(1);
		searchSourceBuilder.query(QueryBuilders
			.boolQuery()
				.filter(QueryBuilders.rangeQuery("@timestamp")
					.gte(gte.toInstant().toEpochMilli())
					.lte(lte.toInstant().toEpochMilli())
					.format("epoch_millis")));
		
		return searchSourceBuilder;
	}
}