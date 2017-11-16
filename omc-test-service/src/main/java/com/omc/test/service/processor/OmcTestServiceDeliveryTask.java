package com.omc.test.service.processor;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcTask;

public class OmcTestServiceDeliveryTask extends OmcTask {

	private final Log logger = LogFactory.getLog(OmcTestServiceDeliveryTask.class);

	public OmcTestServiceDeliveryTask(BlockingQueue<OmcEvent> deliveryQueue) {
		super(deliveryQueue);
	}

	@Override
	public void run() {
		logger.debug("Starting Request Queue worker thread");
		try {
			while(true) {
				OmcEvent omcEvent = this.omcQueue.take();
				logger.debug("Get request from Request Queue: " + omcEvent.toString());
				Thread.sleep(5000);
			}
		} catch (InterruptedException e){
			logger.error(e.getMessage());
		}
	}
}