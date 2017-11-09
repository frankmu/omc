package com.omc.service.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class OmcObserverState {

	private String obname;
	private Integer RQSize;
	private Integer DQSize;
	private Integer MaxRQSize;
	private Integer MaxDQSize;
	private AtomicInteger SuccCount = new AtomicInteger(0);
	private AtomicInteger WarnCount = new AtomicInteger(0);
	private AtomicInteger FailCount = new AtomicInteger(0);

	public String getObname() {
		return obname;
	}
	public Integer getRQSize() {
		return RQSize;
	}
	public Integer getDQSize() {
		return DQSize;
	}
	public Integer getMaxRQSize() {
		return MaxRQSize;
	}
	public Integer getMaxDQSize() {
		return MaxDQSize;
	}
	public Integer getSuccCount() {
		return SuccCount.get();
	}
	public Integer getWarnCount() {
		return WarnCount.get();
	}
	public Integer getFailCount() {
		return FailCount.get();
	}
	public void setObname(String obname) {
		this.obname = obname;
	}
	public void setRQSize(Integer rQSize) {
		RQSize = rQSize;
	}
	public void setDQSize(Integer dQSize) {
		DQSize = dQSize;
	}
	public void setMaxRQSize(Integer maxRQSize) {
		MaxRQSize = maxRQSize;
	}
	public void setMaxDQSize(Integer maxDQSize) {
		MaxDQSize = maxDQSize;
	}
	public void incrementSuccCount() {
		SuccCount.incrementAndGet();
	}
	public void incrementWarnCount() {
		WarnCount.incrementAndGet();
	}
	public void incrementFailCount() {
		FailCount.incrementAndGet();
	}
}