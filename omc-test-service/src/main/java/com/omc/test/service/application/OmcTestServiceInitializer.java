package com.omc.test.service.application;

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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.omc.service.domain.OmcEvent;
import com.omc.service.registration.OmcServiceRegistry;
import com.omc.test.service.processor.OmcTestServiceDeliveryTask;
import com.omc.test.service.processor.OmcTestServiceRequestTask;

@Component
public class OmcTestServiceInitializer {

	@Value("${server.port}")
	private String serverPort;

	@Value("${omc.service.registry.name}")
	private String omcServiceRegistryName;

	@Value("${omc.request.task.thread.size}")
	private int requestTaskThreadSize;

	@Value("${omc.delivery.task.thread.size}")
	private int deliveryTaskThreadSize;

	private final Log logger = LogFactory.getLog(OmcTestServiceConfiguration.class);

	@Autowired
	OmcServiceRegistry omcServiceRegistry;
	
	@Autowired
    private ServletContext servletContext;

	@Autowired
	ThreadPoolTaskExecutor workerExecutor;

	@Autowired
	BlockingQueue<OmcEvent> requestQueue;

	@Autowired
	BlockingQueue<OmcEvent> deliveryQueue;

	@PostConstruct
	public void init() throws UnknownHostException {
		// Start the worker threads
		for(int i = 0; i < requestTaskThreadSize; i++) {
			workerExecutor.execute(new OmcTestServiceRequestTask(requestQueue, deliveryQueue));
		}

		for(int i = 0; i < deliveryTaskThreadSize; i++) {
			workerExecutor.execute(new OmcTestServiceDeliveryTask(deliveryQueue));
		}

		String hostname = InetAddress.getLocalHost().getHostName();
		String uri = hostname + ":" + serverPort + servletContext.getContextPath();
		omcServiceRegistry.registerService(omcServiceRegistryName, uri);
		logger.debug("Register service with path: " + omcServiceRegistryName + ", value: " + uri);
	}

	@PreDestroy
	public void destroy() throws UnknownHostException {
		String hostname = InetAddress.getLocalHost().getHostName();
		String uri = hostname + ":" + serverPort + servletContext.getContextPath();
		omcServiceRegistry.unregisterService(omcServiceRegistryName, uri);
		logger.debug("Unregister service with path: " + omcServiceRegistryName + ", value: " + uri);
	}
}