package com.exathreat.common.config;

import javax.annotation.PostConstruct;

import com.exathreat.common.config.factory.ApplicationSettings;
import com.exathreat.common.config.factory.DebounceSettings;
import com.exathreat.common.config.factory.ElasticsearchSettings;
import com.exathreat.common.config.factory.IdpSettings;
import com.exathreat.common.config.factory.TelegramSettings;

import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ApplicationConfig {

	@Autowired
	private ApplicationSettings applicationSettings;

	@Autowired
	private DebounceSettings debounceSettings;

	@Autowired
	private ElasticsearchSettings elasticsearchSettings;

	@Autowired
	private IdpSettings idpSettings;

	@Autowired
	private TelegramSettings telegramSettings;
	
	@PostConstruct
	public void init() {
		log.info("applicationSettings: " + applicationSettings);
		log.info("debounceSettings: " + debounceSettings);
		log.info("elasticsearchSettings: " + elasticsearchSettings);
		log.info("idpSettings: " + idpSettings);
		log.info("telegramSettings: " + telegramSettings);
	}

	@Bean
	public RestHighLevelClient elasticsearchClient() {
		RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(
			elasticsearchSettings.getDomain(), 
			elasticsearchSettings.getPort(), 
			elasticsearchSettings.getScheme()));

		restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder
			.setDefaultIOReactorConfig(IOReactorConfig.custom()
				.setSoKeepAlive(true)
				.build()));
		
		restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> 
			requestConfigBuilder.setConnectTimeout(elasticsearchSettings.getConnectTimeout())
				.setSocketTimeout(elasticsearchSettings.getSocketTimeout()));

		return new RestHighLevelClient(restClientBuilder);
  }

	@Bean
	public WebClient webClient() {
		return WebClient.create();
	}
}