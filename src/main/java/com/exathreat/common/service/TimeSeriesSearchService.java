package com.exathreat.common.service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

@Service
public class TimeSeriesSearchService {
	private static List<Map<String, TemporalUnit>> timeUnits;
	private static List<Map<String, Object>> timeIntervals;
	static {
		timeUnits = new ArrayList<Map<String, TemporalUnit>>();
		timeUnits.add(Map.of("second", ChronoUnit.SECONDS));
		timeUnits.add(Map.of("minute", ChronoUnit.MINUTES));
		timeUnits.add(Map.of("hour", ChronoUnit.HOURS));
		timeUnits.add(Map.of("day", ChronoUnit.DAYS));
		timeUnits.add(Map.of("week", ChronoUnit.WEEKS));
		timeUnits.add(Map.of("month", ChronoUnit.MONTHS));
		timeUnits.add(Map.of("year", ChronoUnit.YEARS));

		timeIntervals = new ArrayList<Map<String, Object>>();
		timeIntervals.add(Map.of("code", "1s", "name", "1 second", "number", 1, "jvDtFormat", "HH:mm:ss", "from", 1000L, "to", 300000L, "interval", DateHistogramInterval.seconds(1)));												// 1s to 5m - HH:mm:ss
		timeIntervals.add(Map.of("code", "5s", "name", "5 seconds", "number", 5, "jvDtFormat", "HH:mm:ss", "from", 300001L, "to", 1500000L, "interval", DateHistogramInterval.seconds(5)));										// 5m to 25m - HH:mm:ss
		timeIntervals.add(Map.of("code", "10s", "name", "10 seconds", "number", 10, "jvDtFormat", "HH:mm:ss", "from", 1500001L, "to", 3000000L, "interval", DateHistogramInterval.seconds(10)));							// 25m to 50m - HH:mm:ss
		timeIntervals.add(Map.of("code", "30s", "name", "30 seconds", "number", 30, "jvDtFormat", "HH:mm:ss", "from", 3000001L, "to", 9000000L, "interval", DateHistogramInterval.seconds(30)));							// 50m to 2.5h - HH:mm:ss
		timeIntervals.add(Map.of("code", "1m", "name", "1 minute", "number", 1, "jvDtFormat", "HH:mm", "from", 9000001L, "to", 18000000L, "interval", DateHistogramInterval.minutes(1)));											// 2.5h to 5h - HH:mm
		timeIntervals.add(Map.of("code", "5m", "name", "5 minutes", "number", 5, "jvDtFormat", "HH:mm", "from", 18000001L, "to", 90000000L, "interval", DateHistogramInterval.minutes(5)));										// 5h to 1.04d - HH:mm
		timeIntervals.add(Map.of("code", "10m", "name", "10 minutes", "number", 10, "jvDtFormat", "HH:mm", "from", 90000001L, "to", 180000000L, "interval", DateHistogramInterval.minutes(10)));							// 1.04d to 2.08d - HH:mm
		timeIntervals.add(Map.of("code", "30m", "name", "30 minutes", "number", 30, "jvDtFormat", "HH:mm", "from", 180000001L, "to", 540000000L, "interval", DateHistogramInterval.minutes(30)));							// 2.08d to 6.25d - HH:mm
		timeIntervals.add(Map.of("code", "1h", "name", "1 hour", "number", 1, "jvDtFormat", "yyyy-MM-dd HH:mm", "from", 540000001L, "to", 1080000000L, "interval", DateHistogramInterval.hours(1)));					// 6.25d to 12.5d - yyyy-MM-dd HH:mm
		timeIntervals.add(Map.of("code", "3h", "name", "3 hours", "number", 3, "jvDtFormat", "yyyy-MM-dd HH:mm", "from", 1080000001L, "to", 3283000000L, "interval", DateHistogramInterval.hours(3)));				// 12.5d to 1.24M - yyyy-MM-dd HH:mm
		timeIntervals.add(Map.of("code", "12h", "name", "12 hours", "number", 12, "jvDtFormat", "yyyy-MM-dd HH:mm", "from", 3283000001L, "to", 12960000000L, "interval", DateHistogramInterval.hours(12)));		// 1.24M to 4.93M - yyyy-MM-dd HH:mm
		timeIntervals.add(Map.of("code", "1d", "name", "1 day", "number", 1, "jvDtFormat", "yyyy-MM-dd HH:mm", "from", 12960000001L, "to", 26280000000L, "interval", DateHistogramInterval.days(1)));					// 1.24M to 10M - yyyy-MM-dd HH:mm
		timeIntervals.add(Map.of("code", "7d", "name", "7 days", "number", 7, "jvDtFormat", "yyyy-MM-dd HH:mm", "from", 26280000001L, "to", 189200000000L, "interval", DateHistogramInterval.days(7)));				// 10M to 6Y - yyyy-MM-dd HH:mm
		timeIntervals.add(Map.of("code", "30d", "name", "30 days", "number", 30, "jvDtFormat", "yyyy-MM-dd HH:mm", "from", 189200000001L, "to", 788400000000L, "interval", DateHistogramInterval.days(30)));	// 6Y to 25Y - yyyy-MM-dd HH:mm
		timeIntervals.add(Map.of("code", "90d", "name", "90 days", "number", 90, "jvDtFormat", "yyyy-MM-dd HH:mm", "from", 788400000001L, "to", 2365000000000L, "interval", DateHistogramInterval.days(90)));	// 25Y to 75Y - yyyy-MM-dd HH:mm
	}

	public Map<String, Object> getDateRangeInfo(String dateRange, String dateRangeFormat, String intervalUnit, ModelMap modelMap) throws Exception {
		Map<String, Object> dateRangeInfo = new HashMap<String, Object>();

		ZonedDateTime gte = null;
		ZonedDateTime lte = null;

		// if dateRange is 'relative time' ...

		if (dateRange.startsWith("Last")) {
			String[] dateRangeArray = dateRange.split(" ");
			Integer timePeriod = Integer.parseInt(dateRangeArray[1]);
			String timeUnit = (dateRangeArray[2].endsWith("s")) ? dateRangeArray[2].substring(0, dateRangeArray[2].length()-1) : dateRangeArray[2];

			TemporalUnit temporalUnit = null;
			for (Map<String, TemporalUnit> ti : timeUnits) {
				if (ti.containsKey(timeUnit)) {
					temporalUnit = ti.get(timeUnit);
					break;
				}
			}
			gte = ZonedDateTime.now(ZoneOffset.UTC).minus(timePeriod, temporalUnit);
			lte = ZonedDateTime.now(ZoneOffset.UTC);
		}

		// else if dateRange is 'absolute time' ...

		else {
			String[] dateRangeArray = dateRange.split(" to ");

			DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern(dateRangeFormat, Locale.ENGLISH).withZone(ZoneId.of((String) modelMap.get("timezone")));

			gte = ZonedDateTime.parse(dateRangeArray[0], dtFormatter).withZoneSameInstant(ZoneOffset.UTC);
			lte = ZonedDateTime.parse(dateRangeArray[1], dtFormatter).withZoneSameInstant(ZoneOffset.UTC);
		}
		
		Map<String, Object> timeInterval = null;
		for (Map<String, Object> ti : timeIntervals) {
			String code = (String) ti.get("code");
			Long from = (Long) ti.get("from");
			Long to = (Long) ti.get("to");

			if ("auto".equalsIgnoreCase(intervalUnit)) {
				Long rangeInMs = (lte.toInstant().toEpochMilli() - gte.toInstant().toEpochMilli());

				if (rangeInMs >= from && rangeInMs <= to) {
					timeInterval = ti;
					break;
				}
			}
			else {
				if (code.equalsIgnoreCase(intervalUnit)) {
					timeInterval = ti;
					break;
				}
			}
		}

		dateRangeInfo.put("gte", gte);
		dateRangeInfo.put("lte", lte);
		dateRangeInfo.put("timeInterval", timeInterval);
		return dateRangeInfo;
	}
}