package com.omc.service.domain;

public abstract class OmcTask implements Runnable {

	private OmcEvent omcEvent;

	public OmcTask(OmcEvent omcEvent) {
		this.omcEvent = omcEvent;
	}

	public OmcEvent getOmcEvent() {
		return omcEvent;
	}
}