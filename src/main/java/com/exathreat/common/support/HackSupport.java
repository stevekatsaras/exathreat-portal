package com.exathreat.common.support;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

@Component
public class HackSupport {

	/**
	 * this is a hack!!!
	 * this method converts the 'zonedDateTime' to the 'timezone' of your choice; then ...
	 * this method attaches UTC to this 'timezoned' timestamp, effectively making it now a 'fake' UTC timestamp.
	 * 
	 * this is required for ApexCharts; this library is limited to UTC and Exathreat has support for all timezones.
	 * credit: James Kozik.
	 */
	public OffsetDateTime hackUTCOnZoneDateTime(ZonedDateTime zonedDateTime, String timezone) {
		ZonedDateTime sdtz = zonedDateTime.withZoneSameInstant(ZoneId.of(timezone));
		OffsetDateTime utctz = sdtz.toOffsetDateTime().withOffsetSameLocal(ZoneOffset.UTC);
		return utctz;
	}
}