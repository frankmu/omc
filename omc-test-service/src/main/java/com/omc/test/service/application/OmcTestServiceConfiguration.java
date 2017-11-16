package com.omc.test.service.application;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.omc.service.discovery.OmcServiceDiscovery;
import com.omc.service.discovery.OmcServiceDiscovery.DiscoveryMode;
import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcObserverState;
import com.omc.service.registration.OmcServiceRegistry;
import com.omc.service.registration.OmcServiceRegistry.RegistryMode;
import com.omc.service.zookeeper.OmcZookeeperServiceRegistry;

@Configuration
public class OmcTestServiceConfiguration {

	@Value("${omc.obname}")
	private String obname;

	@Value("${omc.request.queue.max.size}")
	private int MaxRQSize;

	@Value("${omc.delivery.queue.max.size}")
	private int MaxDQSize;

	@Value("${omc.request.task.thread.size}")
	private int requestTaskThreadSize;

	@Value("${omc.delivery.task.thread.size}")
	private int deliveryTaskThreadSize;

	@Value("${zookeeper.host}")
	private String zookeeperHost;

	@Value("${zookeeper.port}")
	private String zookeeperPort;

	@Value("${omc.service.registry.mode}")
	private String registryMode;

	@Value("${omc.service.discovery.mode}")
	private String discoveryMode;

	private final Log logger = LogFactory.getLog(OmcTestServiceConfiguration.class);

	@Bean
    public OmcObserverState omcObserverState() {
        return new OmcObserverState(obname, MaxRQSize, MaxDQSize);
    }

	@Bean
    public BlockingQueue<OmcEvent> requestQueue() {
		logger.debug("Initialize Request Queue with max size of: " + MaxRQSize);
        return new LinkedBlockingQueue<>(MaxRQSize);
    }

	@Bean
    public BlockingQueue<OmcEvent> deliveryQueue() {
		logger.debug("Initialize Delivery Queue with max size of: " + MaxDQSize);
        return new LinkedBlockingQueue<>(MaxDQSize);
    }

	@Bean
    public OmcZookeeperServiceRegistry OmcZookeeperServiceRegistry() {
		if(RegistryMode.ZOOKEEPER.toString().equalsIgnoreCase(registryMode) || DiscoveryMode.ZOOKEEPER.toString().equalsIgnoreCase(discoveryMode)) {
			return new OmcZookeeperServiceRegistry(zookeeperHost, zookeeperPort);
		}
		return null;
    }

	@Bean
    public OmcServiceRegistry omcServiceRegistry() {
		if(RegistryMode.ZOOKEEPER.toString().equalsIgnoreCase(registryMode)) {
			return OmcZookeeperServiceRegistry();
		}
		return null;
    }

	@Bean
    public OmcServiceDiscovery omcServiceDiscovery() {
		if(DiscoveryMode.ZOOKEEPER.toString().equalsIgnoreCase(discoveryMode)) {
			return OmcZookeeperServiceRegistry();
		}
		return null;
    }

	@Bean
	public ThreadPoolTaskExecutor workerExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		int totalThreadSize = requestTaskThreadSize + deliveryTaskThreadSize;
		pool.setCorePoolSize(totalThreadSize);
		pool.setThreadNamePrefix("OMC-Worker-");
		pool.setWaitForTasksToCompleteOnShutdown(true);
		logger.debug("Initialize Worker Executor with pool size: " + totalThreadSize);
		return pool;
	}
}