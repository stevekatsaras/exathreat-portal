package com.exathreat.common.config.factory;

import java.util.Map;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ElasticsearchFactory {
	private ObjectMapper objectMapper = new ObjectMapper();
	private TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>(){};

	@Autowired
	private RestHighLevelClient elasticsearchClient;

	@PostConstruct
	public void init() {
		initExathreatIntelIndex("exathreat-intel", "exathreat-intel-000001");	// create exathreat-intel index (if not found - usually will occur in new environments)
	}

	private void initExathreatIntelIndex(String alias, String index) {
		try {
			boolean exists = elasticsearchClient.indices().exists(new GetIndexRequest(alias), RequestOptions.DEFAULT);
			if (!exists) {
				log.info("Creating " + alias + " elasticsearch index...");
				createIndex(alias, index, 1);
			}
		}
		catch (Exception exception) {}
	}
	
	public void createIndex(String esAlias, String esIndex, int numOfShards) throws Exception {
		ObjectNode mappingsNode = objectMapper.createObjectNode();
		mappingsNode.putObject("properties")
			.putObject("event")
				.put("type", "text")
					.putObject("fields")
						.putObject("keyword")
							.put("type", "keyword");
		
		CreateIndexRequest createIndexRequest = new CreateIndexRequest(esIndex);
		createIndexRequest.alias(new Alias(esAlias));
		createIndexRequest.mapping(objectMapper.convertValue(mappingsNode, typeReference));
		createIndexRequest.settings(Settings.builder()
			.put("index.number_of_shards", numOfShards)
			.put("index.mapping.ignore_malformed", true));
		
		elasticsearchClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
	}

	public GetMappingsResponse getFieldMappings(String indexAlias) throws Exception {
		return elasticsearchClient.indices().getMapping(new GetMappingsRequest().indices(indexAlias), RequestOptions.DEFAULT);
	}

	public SearchResponse searchIndex(SearchRequest searchRequest) throws Exception {
		return elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
	}

	public SearchResponse searchScroll(String scrollId) throws Exception {
		SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
		searchScrollRequest.scroll(TimeValue.timeValueMinutes(1));

		return elasticsearchClient.scroll(searchScrollRequest, RequestOptions.DEFAULT);
	}
	
	public void clearScroll(String scrollId) throws Exception {
		if (scrollId != null) {
			ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
			clearScrollRequest.addScrollId(scrollId);
	
			elasticsearchClient.clearScrollAsync(clearScrollRequest, RequestOptions.DEFAULT, new ActionListener<ClearScrollResponse>() {
				@Override
				public void onResponse(ClearScrollResponse response) {}
				
				@Override
				public void onFailure(Exception e) {}
			});
		}
	}
	
}