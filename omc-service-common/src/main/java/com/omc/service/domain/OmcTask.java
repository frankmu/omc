package com.omc.service.domain;

import java.util.concurrent.BlockingQueue;

public abstract class OmcTask implements Runnable {

	protected BlockingQueue<OmcEvent> omcQueue;

	public OmcTask(BlockingQueue<OmcEvent> omcQueue) {
		this.omcQueue = omcQueue;
	}

	public BlockingQueue<OmcEvent> getOmcQueue() {
		return omcQueue;
	}
}