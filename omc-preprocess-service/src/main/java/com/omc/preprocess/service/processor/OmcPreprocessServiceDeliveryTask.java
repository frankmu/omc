package com.omc.preprocess.service.processor;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestTemplate;

import com.omc.service.discovery.OmcServiceDiscovery;
import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcObserverState;
import com.omc.service.domain.OmcObserver.ResponseState;
import com.omc.service.domain.OmcTask;
import com.omc.service.util.OmcEventUtil;

public class OmcPreprocessServiceDeliveryTask extends OmcTask {

	private static String EMPTY_DELIVERY_MODE="none";
	private final Log logger = LogFactory.getLog(OmcPreprocessServiceDeliveryTask.class);
	private OmcServiceDiscovery omcServiceDiscovery;
	private String deliveryMode;
	private OmcObserverState omcObserverState;
	private int deliveryRetryCount;

	public OmcPreprocessServiceDeliveryTask(BlockingQueue<OmcEvent> deliveryQueue, 
			OmcServiceDiscovery omcServiceDiscovery,
			String deliveryMode,
			OmcObserverState omcObserverState,
			int deliveryRetryCount) {
		super(deliveryQueue);
		this.omcServiceDiscovery = omcServiceDiscovery;
		this.deliveryMode = deliveryMode;
		this.omcObserverState = omcObserverState;
		this.deliveryRetryCount = deliveryRetryCount;
	}

	@Override
	public void run() {
		logger.debug("Starting Delivery Queue worker thread");
		while (true) {
			try {
				OmcEvent omcEvent = this.omcQueue.take();
				logger.debug("Get task from Delivery Queue: " + omcEvent.toString());
				if (process(omcEvent)) {
					omcObserverState.incrementSuccCount();
					OmcEventUtil.updateObserverDeliveryState(omcEvent, ResponseState.SUCCESS);
					logger.debug("Successfully deliveried event: " + omcEvent.toString());
				} else {
					logger.debug("Exceeded max retry count, delivery service error!");
					break;
				}
			} catch (InterruptedException e) {
				logger.error("Thread was interrupted!");
				break;
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		logger.debug("Delivery Queue worker thread stopped!");
	}

	private boolean process(OmcEvent omcEvent) {
		int retryCount = 0;
		while(retryCount <= deliveryRetryCount) {
			try {
				Thread.sleep(5000);
				if (deliveryMode != null && !EMPTY_DELIVERY_MODE.equalsIgnoreCase(deliveryMode)) {
					String uri = omcServiceDiscovery.discoverServiceURI(deliveryMode);
					RestTemplate restTemplate = new RestTemplate();
					restTemplate.postForEntity("http://" + uri + "/go", omcEvent, boolean.class);
				}
				return true;
			} catch (Exception e) {
				omcObserverState.incrementFailCount();
				logger.error(e.getMessage() + " [Retry " + retryCount++ + "/" + deliveryRetryCount + "]");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					logger.error("Thread was interrupted!");
				}
			}
		}
		return false;
	}
}