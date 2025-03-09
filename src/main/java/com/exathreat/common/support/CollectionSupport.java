package com.exathreat.common.support;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class CollectionSupport {

	@SuppressWarnings (value="unchecked")
	public void flattenSearchHitMap(String currentKey, Map<String, Object> source, Map<String, Object> target) {
		for (Map.Entry<String, Object> entry : source.entrySet()) {
			if (entry.getValue() instanceof Map) {
				flattenSearchHitMap(currentKey + "." + entry.getKey(), (Map<String, Object>) entry.getValue(), target);
			}
			else {
				target.put((currentKey + "." + entry.getKey()).substring(1), entry.getValue());
			}
		}
	}

	@SuppressWarnings (value="unchecked")
	public void flattenFieldMappingMap(String currentKey, Map<String, Object> source, Map<String, Object> target) {
		for (Map.Entry<String, Object> entry : source.entrySet()) {
			Map<String, Object> map = (Map<String, Object>) entry.getValue();
			if (map.containsKey("properties")) {
				flattenFieldMappingMap(currentKey + "." + entry.getKey(), (Map<String, Object>) map.get("properties"), target);
			}
			else {
				target.put((currentKey + "." + entry.getKey()).substring(1), map.get("type"));
			}
		}
	}
	
}