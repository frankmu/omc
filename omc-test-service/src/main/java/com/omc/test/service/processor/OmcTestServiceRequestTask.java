package com.omc.test.service.processor;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcTask;

public class OmcTestServiceRequestTask extends OmcTask {

	private BlockingQueue<OmcEvent> deliveryQueue;

	private final Log logger = LogFactory.getLog(OmcTestServiceRequestTask.class);

	public OmcTestServiceRequestTask(BlockingQueue<OmcEvent> requestQueue, BlockingQueue<OmcEvent> deliveryQueue) {
		super(requestQueue);
		this.deliveryQueue = deliveryQueue;
	}

	@Override
	public void run() {
		logger.debug("Starting Request Queue worker thread");
		while (true) {
			try {
				OmcEvent omcEvent = this.omcQueue.take();
				logger.debug("Get task from Request Queue: " + omcEvent.toString());
				Thread.sleep(5000);
				deliveryQueue.put(omcEvent);
			} catch (InterruptedException e) {
				logger.error("Thread was interrupted!");
				break;
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
}