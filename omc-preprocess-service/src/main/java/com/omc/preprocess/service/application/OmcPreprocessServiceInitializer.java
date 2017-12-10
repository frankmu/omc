package com.omc.preprocess.service.application;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.omc.preprocess.service.client.rule.OmcPreprocessServiceRules;
import com.omc.preprocess.service.processor.OmcPreprocessServiceDeliveryTask;
import com.omc.preprocess.service.processor.OmcPreprocessServiceRequestTask;
import com.omc.service.discovery.OmcServiceDiscovery;
import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcObserverProperties;
import com.omc.service.domain.OmcObserverState;
import com.omc.service.registration.OmcServiceRegistry;

@Component
public class OmcPreprocessServiceInitializer {

	@Value("${omc.service.registry.name:}")
	private String omcServiceRegistryName;

	@Value("${omc.request.task.thread.size}")
	private int requestTaskThreadSize;

	@Value("${omc.delivery.task.thread.size}")
	private int deliveryTaskThreadSize;

	@Value("${omc.delivery.mode:}")
	private String deliveryMode;

	@Value("${omc.delivery.retry.count:0}")
	private int deliveryRetryCount;

	private final Log logger = LogFactory.getLog(OmcPreprocessServiceConfiguration.class);

	@Autowired
	OmcObserverProperties omcObserverProperties;

	@Autowired
	OmcServiceRegistry omcServiceRegistry;

	@Autowired
	OmcServiceDiscovery omcServiceDiscovery;
	
	@Autowired
    private ServletContext servletContext;

	@Autowired
	ThreadPoolTaskExecutor workerExecutor;

	@Autowired
	BlockingQueue<OmcEvent> requestQueue;

	@Autowired
	BlockingQueue<OmcEvent> deliveryQueue;

	@Autowired
	OmcObserverState omcObserverState;

	@Autowired
	OmcPreprocessServiceRules omcPreprocessServiceRules;

	@PostConstruct
	public void init() throws UnknownHostException {
		// Start the worker threads
		for(int i = 0; i < requestTaskThreadSize; i++) {
			workerExecutor.execute(new OmcPreprocessServiceRequestTask(requestQueue, deliveryQueue, omcPreprocessServiceRules));
		}

		for(int i = 0; i < deliveryTaskThreadSize; i++) {
			workerExecutor.execute(new OmcPreprocessServiceDeliveryTask(deliveryQueue, omcServiceDiscovery, deliveryMode, omcObserverState, deliveryRetryCount));
		}

		if(omcServiceRegistry != null) {
			String hostname = InetAddress.getLocalHost().getHostName();
			String uri = hostname + ":" + omcObserverProperties.getServerPort() + servletContext.getContextPath();
			omcServiceRegistry.registerService(omcServiceRegistryName, uri);
			logger.debug("Register service with path: " + omcServiceRegistryName + ", value: " + uri);
		}
	}

	@PreDestroy
	public void destroy() throws UnknownHostException {
		if(omcServiceRegistry != null) {
			String hostname = InetAddress.getLocalHost().getHostName();
			String uri = hostname + ":" + omcObserverProperties.getServerPort() + servletContext.getContextPath();
			omcServiceRegistry.unregisterService(omcServiceRegistryName, uri);
			logger.debug("Unregister service with path: " + omcServiceRegistryName + ", value: " + uri);
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		logger.info("Service " + omcObserverProperties.getObname() + " has been started successfully!");
	}
}