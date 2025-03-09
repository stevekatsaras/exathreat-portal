package com.exathreat.common.support;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;

@Component
public class JsonSupport {
	private ObjectMapper objectMapper = new ObjectMapper();

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}
		
	public Map<String, Object> toHashMap(String json) throws Exception {
		return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
	}

	public Map<String, Object> toHashMap(Object value) throws Exception {
		return objectMapper.convertValue(value, new TypeReference<Map<String, Object>>() {});
	}

	@SuppressWarnings("unchecked")
	public <T> T toBean(Map<String, Object> src, Class<?> beanType) {
		 return (T) objectMapper.convertValue(src, beanType);
	}
	
}