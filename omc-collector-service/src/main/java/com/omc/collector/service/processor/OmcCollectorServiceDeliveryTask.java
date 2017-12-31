package com.omc.collector.service.processor;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

import com.omc.service.discovery.OmcServiceDiscovery;
import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcObserverState;
import com.omc.service.domain.OmcObserver.ResponseState;
import com.omc.service.domain.OmcTask;
import com.omc.service.util.OmcEventUtil;

public class OmcCollectorServiceDeliveryTask extends OmcTask {

	private static String EMPTY_DELIVERY_MODE="none";
	private final Log logger = LogFactory.getLog(OmcCollectorServiceDeliveryTask.class);
	private OmcServiceDiscovery omcServiceDiscovery;
	private String deliveryMode;
	private OmcObserverState omcObserverState;
	private int deliveryRetryCount;
	private AsyncRestTemplate restTemplate;

	public OmcCollectorServiceDeliveryTask(BlockingQueue<OmcEvent> deliveryQueue, 
			OmcServiceDiscovery omcServiceDiscovery,
			String deliveryMode,
			OmcObserverState omcObserverState,
			int deliveryRetryCount,
			AsyncRestTemplate restTemplate) {
		super(deliveryQueue);
		this.omcServiceDiscovery = omcServiceDiscovery;
		this.deliveryMode = deliveryMode;
		this.omcObserverState = omcObserverState;
		this.deliveryRetryCount = deliveryRetryCount;
		this.restTemplate = restTemplate;
	}

	@Override
	public void run() {
		logger.debug("Starting Delivery Queue worker thread");
		while (true) {
			try {
				OmcEvent omcEvent = this.omcQueue.take();
				logger.debug("Get task from Delivery Queue: " + omcEvent.toString());
				process(omcEvent);
			} catch (InterruptedException e) {
				logger.error("Thread was interrupted!");
				break;
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		logger.debug("Delivery Queue worker thread stopped!");
	}

	private void process(final OmcEvent omcEvent) {
		if(omcObserverState.getFailCount() <= deliveryRetryCount) {
			try {
				OmcEventUtil.updateObserverDeliveryState(omcEvent, ResponseState.SUCCESS);
				if (deliveryMode != null && !EMPTY_DELIVERY_MODE.equalsIgnoreCase(deliveryMode)) {
					String uri = omcServiceDiscovery.discoverServiceURI(deliveryMode);
					HttpEntity<OmcEvent> entity = new HttpEntity<OmcEvent>(omcEvent);
					ListenableFuture<ResponseEntity<Boolean>> futureEntity = restTemplate.postForEntity("http://" + uri + "/go", entity, Boolean.class);
					futureEntity.addCallback(new ListenableFutureCallback<ResponseEntity<Boolean>>() {
				        @Override
						public void onSuccess(ResponseEntity<Boolean> result) {
							omcObserverState.incrementSuccCount();
							logger.debug("Successfully deliveried event: " + omcEvent.toString());
						}

				        @Override
						public void onFailure(Throwable ex) {
							omcObserverState.incrementFailCount();
							logger.error(ex.getMessage());
							// Recursion call to process function when error happens
							process(omcEvent);
						}
				    });
				}
			} catch (Exception e) {
				omcObserverState.incrementFailCount();
				logger.error(e.getMessage());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
					logger.error("Thread was interrupted!");
				}
			}
		} else {
			logger.error("Max fail count reached, need to restart the service.");
		}
	}
}