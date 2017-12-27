package com.omc.collector.service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class OmcCollectorServiceUtils {

	private static final Log logger = LogFactory.getLog(OmcCollectorServiceUtils.class);
	private static AtomicInteger counter = new AtomicInteger(0);

	public static Long getFormattedTimestamp(String timestamp, String pattern) {
		try {
			SimpleDateFormat dt = new SimpleDateFormat(pattern);
			Date date = dt.parse(timestamp);
			if (!pattern.contains("yyyy")) {
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
				date = c.getTime();
			}
			return date.getTime() / 1000L;
		} catch (ParseException e) {
			logger.error("Cannot convert timestamp: " + timestamp + " using pattern: " + pattern + ". Error: " + e.getMessage());
			return null;
		}
	}

	public static String generateEventId(String obname) {
		Instant now = Instant.now(new OmcCollectorServiceNanoClock());
		String timestamp = String.valueOf(now.getEpochSecond());
		String nano = String.valueOf(now.getNano());
		return obname + "-" + counter.getAndIncrement() + "-"+ timestamp + "." + nano;
	}
}
