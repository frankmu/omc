package com.omc.test.service.processor;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestTemplate;

import com.omc.service.discovery.OmcServiceDiscovery;
import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcObserver.ResponseState;
import com.omc.service.domain.OmcTask;
import com.omc.service.util.OmcEventUtil;

public class OmcTestServiceDeliveryTask extends OmcTask {

	private static String EMPTY_DELIVERY_MODE="none";
	private final Log logger = LogFactory.getLog(OmcTestServiceDeliveryTask.class);
	private OmcServiceDiscovery omcServiceDiscovery;
	private String deliveryMode;

	public OmcTestServiceDeliveryTask(BlockingQueue<OmcEvent> deliveryQueue, OmcServiceDiscovery omcServiceDiscovery, String deliveryMode) {
		super(deliveryQueue);
		this.omcServiceDiscovery = omcServiceDiscovery;
		this.deliveryMode = deliveryMode;
	}

	@Override
	public void run() {
		logger.debug("Starting Delivery Queue worker thread");
		try {
			while(true) {
				OmcEvent omcEvent = this.omcQueue.take();
				logger.debug("Get task from Delivery Queue: " + omcEvent.toString());
				Thread.sleep(5000);
				OmcEventUtil.updateObserverDeliveryState(omcEvent, ResponseState.SUCCESS);
				if(deliveryMode != null && !EMPTY_DELIVERY_MODE.equalsIgnoreCase(deliveryMode)) {
					String uri = omcServiceDiscovery.discoverServiceURI(deliveryMode);
					RestTemplate restTemplate = new RestTemplate();
					restTemplate.postForEntity("http://" + uri + "/go", omcEvent, boolean.class);
				}
				logger.debug("Successfully deliveried event: " + omcEvent.toString());
			}
		} catch (InterruptedException e){
			logger.error(e.getMessage());
		}
	}
}