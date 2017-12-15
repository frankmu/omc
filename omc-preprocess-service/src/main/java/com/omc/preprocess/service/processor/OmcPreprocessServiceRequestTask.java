package com.omc.preprocess.service.processor;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.omc.preprocess.service.client.rule.OmcPreprocessServiceRules;
import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcTask;

public class OmcPreprocessServiceRequestTask extends OmcTask{
	
	private BlockingQueue<OmcEvent> deliveryQueue;
	private OmcPreprocessServiceRules omcPreprocessServiceRules;

	private final Log logger = LogFactory.getLog(OmcPreprocessServiceRequestTask.class);

	public OmcPreprocessServiceRequestTask(BlockingQueue<OmcEvent> requestQueue, BlockingQueue<OmcEvent> deliveryQueue, OmcPreprocessServiceRules omcPreprocessServiceRules) {
		super(requestQueue);
		this.deliveryQueue = deliveryQueue;
		this.omcPreprocessServiceRules = omcPreprocessServiceRules;
	}

	@Override
	public void run() {
		logger.debug("Starting Request Queue worker thread");
		while (true) {
			try {
				OmcEvent omcEvent = this.omcQueue.take();
				logger.debug("Get task from Request Queue: " + omcEvent.toString());
				Thread.sleep(5000);
				OmcEvent result = process(omcEvent);
				deliveryQueue.put(result);
			} catch (InterruptedException e) {
				logger.error("Thread was interrupted!");
				break;
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	private OmcEvent process(OmcEvent omcEvent) {
		Map<String, Object> data = omcPreprocessServiceRules.parse(omcEvent.getData());
		omcEvent.setData(data);
		return omcEvent;
	}
}