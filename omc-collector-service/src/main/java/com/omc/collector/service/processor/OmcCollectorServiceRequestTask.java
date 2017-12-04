package com.omc.collector.service.processor;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.omc.service.domain.OmcEvent;

public class OmcCollectorServiceRequestTask implements Runnable{
	
	private BlockingQueue<String> requestQueue;
	private BlockingQueue<OmcEvent> deliveryQueue;

	private final Log logger = LogFactory.getLog(OmcCollectorServiceRequestTask.class);

	public OmcCollectorServiceRequestTask(BlockingQueue<String> requestQueue, BlockingQueue<OmcEvent> deliveryQueue) {
		this.requestQueue = requestQueue;
		this.deliveryQueue = deliveryQueue;
	}

	@Override
	public void run() {
		logger.debug("Starting Request Queue worker thread");
		while (true) {
			try {
				String message = this.requestQueue.take();
				logger.debug("Get task from Request Queue: " + message);
				Thread.sleep(5000);
				//deliveryQueue.put(omcEvent);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
}