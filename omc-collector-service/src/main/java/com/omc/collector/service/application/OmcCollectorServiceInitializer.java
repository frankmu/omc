package com.omc.collector.service.application;

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

import com.omc.collector.service.processor.OmcCollectorServiceDeliveryTask;
import com.omc.collector.service.processor.OmcCollectorServiceManager;
import com.omc.collector.service.processor.OmcCollectorServiceRequestTask;
import com.omc.collector.service.processor.OmcCollectorServiceShellCommandTask;
import com.omc.service.discovery.OmcServiceDiscovery;
import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcObserverProperties;
import com.omc.service.domain.OmcObserverState;
import com.omc.service.registration.OmcServiceRegistry;

@Component
public class OmcCollectorServiceInitializer {

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

	@Value("${omc.collector.service.shell.command:}")
	private String shellCommand;

	private final Log logger = LogFactory.getLog(OmcCollectorServiceConfiguration.class);

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
	BlockingQueue<String> requestQueue;

	@Autowired
	BlockingQueue<OmcEvent> deliveryQueue;

	@Autowired
	OmcObserverState omcObserverState;

	@Autowired
	OmcCollectorServiceManager omcCollectorServiceManager;

	@PostConstruct
	public void init() throws UnknownHostException {
		// Start the worker threads
		for(int i = 0; i < requestTaskThreadSize; i++) {
			workerExecutor.execute(new OmcCollectorServiceRequestTask(requestQueue, deliveryQueue, omcCollectorServiceManager));
		}

		for(int i = 0; i < deliveryTaskThreadSize; i++) {
			workerExecutor.execute(new OmcCollectorServiceDeliveryTask(deliveryQueue, omcServiceDiscovery, deliveryMode, omcObserverState, deliveryRetryCount));
		}

		workerExecutor.execute(new OmcCollectorServiceShellCommandTask(requestQueue, shellCommand));

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