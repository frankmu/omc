package com.omc.collector.service.util;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class OmcCollectorServiceNanoClock extends Clock {
	private final Clock clock;
	private final long initialNanos;
	private final Instant initialInstant;

	public OmcCollectorServiceNanoClock() {
		this(Clock.systemUTC());
	}

	public OmcCollectorServiceNanoClock(final Clock clock) {
		this.clock = clock;
		initialInstant = clock.instant();
		initialNanos = getSystemNanos();
	}

	@Override
	public ZoneId getZone() {
		return clock.getZone();
	}

	@Override
	public Instant instant() {
		return initialInstant.plusNanos(getSystemNanos() - initialNanos);
	}

	@Override
	public Clock withZone(final ZoneId zone) {
		return new OmcCollectorServiceNanoClock(clock.withZone(zone));
	}

	private long getSystemNanos() {
		return System.nanoTime();
	}
}