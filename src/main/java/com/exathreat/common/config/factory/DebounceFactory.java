package com.exathreat.common.config.factory;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class DebounceFactory {
	private ParameterizedTypeReference<Map<String, Object>> typeReference = new ParameterizedTypeReference<Map<String, Object>>(){};

	@Autowired
	private DebounceSettings debounceSettings;

	@Autowired
	private WebClient webClient;
	
	public boolean isDisposableEmailAddress(String emailAddress) throws Exception {
		boolean disposable = false;
		try {
			Map<String, Object> responseBody = webClient.get()
				.uri(debounceSettings.getRootUri() + "/?email=" + emailAddress)
				.retrieve()
				.bodyToMono(typeReference)
				.block();
							
			disposable = Boolean.valueOf((String)responseBody.get("disposable"));
		}
		catch (Exception exception) {
			System.err.println("Error calling debounce.io - reason: " + exception.getMessage());
		}
		return disposable;
	}
}