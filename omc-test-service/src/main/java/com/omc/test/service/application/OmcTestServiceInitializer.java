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
import org.springframework.core.task.AsyncTaskExecutor;
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

	private final Log logger = LogFactory.getLog(OmcTestServiceConfiguration.class);

	@Autowired
	OmcServiceRegistry omcServiceRegistry;
	
	@Autowired
    private ServletContext servletContext;

	@Autowired
	AsyncTaskExecutor managerExecutor;

	@Autowired
	ThreadPoolTaskExecutor workerExecutor;

	@Autowired
	BlockingQueue<OmcEvent> requestQueue;

	@Autowired
	BlockingQueue<OmcEvent> deliveryQueue;

	@PostConstruct
	public void init() throws UnknownHostException {
		//managerExecutor.execute(new requestQueueDispatcher());
		//managerExecutor.execute(new deliveryQueueDispatcher());

		String ip = InetAddress.getLocalHost().getHostAddress();
		String uri = ip + "/" + serverPort + servletContext.getContextPath();
		omcServiceRegistry.registerService(omcServiceRegistryName, uri);
		logger.debug("Register service with path: " + omcServiceRegistryName + ", value: " + uri);
	}

	@PreDestroy
	public void destroy() throws UnknownHostException {
		String ip = InetAddress.getLocalHost().getHostAddress();
		String uri = ip + "/" + serverPort + servletContext.getContextPath();
		omcServiceRegistry.unregisterService(omcServiceRegistryName, uri);
		logger.debug("Unregister service with path: " + omcServiceRegistryName + ", value: " + uri);
	}

	public class requestQueueDispatcher implements Runnable{
		@Override
		public void run() {
			logger.debug("Starting Request Queue Dispatcher thread");
			try {
				while(true) {
					OmcEvent omcEvent = requestQueue.take();
					logger.debug("Request Queue Dispatcher get a request with data: " + omcEvent.toString());
					workerExecutor.execute(new OmcTestServiceRequestTask(omcEvent));
				}
			} catch (InterruptedException e){
				logger.error(e.getMessage());
			}
		}
	}

	public class deliveryQueueDispatcher implements Runnable{
		@Override
		public void run() {
			logger.debug("Starting Delivery Queue Dispatcher thread");
			try {
				while(true) {
					OmcEvent omcEvent = deliveryQueue.take();
					logger.debug("Deliveryt Queue Dispatcher get a request with data: " + omcEvent.toString());
					workerExecutor.execute(new OmcTestServiceDeliveryTask(omcEvent));
				}
			} catch (InterruptedException e){
				logger.error(e.getMessage());
			}
		}
	}
}