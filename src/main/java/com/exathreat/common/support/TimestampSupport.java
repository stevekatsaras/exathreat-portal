package com.exathreat.common.support;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class TimestampSupport {

	public String format(ZonedDateTime zonedDateTime, String timezone, String pattern) throws Exception {
		String timestampStr = "";
		if (zonedDateTime != null) {
			timestampStr = zonedDateTime.withZoneSameInstant(ZoneId.of(timezone)).format(DateTimeFormatter.ofPattern(pattern));
		}
		return timestampStr.replaceAll("\\.", "");
	}

	public String subtractDays(ZonedDateTime zonedDateTime, long days, String pattern) throws Exception {
		String timestampStr = "";
		if (zonedDateTime != null) {
			timestampStr = zonedDateTime.minusDays(days).format(DateTimeFormatter.ofPattern(pattern));
		}
		return timestampStr.replaceAll("\\.", "");
	}

	public String utcParse(String dateTimeStr, String timezone, String pattern) throws Exception {
		String timestampStr = "";
		if (dateTimeStr != null) {
			timestampStr = ZonedDateTime.ofInstant(Instant.parse(dateTimeStr), ZoneId.of(timezone)).format(DateTimeFormatter.ofPattern(pattern));
		}
		return timestampStr.replaceAll("\\.", "");
	}
	
}