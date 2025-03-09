package com.exathreat.common.jpa.converter;

import java.util.Map;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapConverter implements AttributeConverter<Map<String, Object>, String> {
	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convertToDatabaseColumn(Map<String, Object> attribute) {
		String jsonStr = null;
		try {
			jsonStr = objectMapper.writeValueAsString(attribute);
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		return jsonStr;
	}

	@Override
	public Map<String, Object> convertToEntityAttribute(String dbData) {
		Map<String, Object> map = null;
		try {
			map = objectMapper.readValue(dbData, new TypeReference<Map<String, Object>>() {});
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		return map;
	}
}