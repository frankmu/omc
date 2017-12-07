package com.omc.collector.service.application;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.omc.collector.service.processor.OmcCollectorServiceManager;
import com.omc.service.discovery.OmcServiceDiscovery;
import com.omc.service.discovery.OmcServiceDiscovery.DiscoveryMode;
import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcObserverProperties;
import com.omc.service.domain.OmcObserverState;
import com.omc.service.registration.OmcServiceRegistry;
import com.omc.service.registration.OmcServiceRegistry.RegistryMode;
import com.omc.service.util.OmcCommonUtil;
import com.omc.service.zookeeper.OmcZookeeperServiceRegistry;

@Configuration
public class OmcCollectorServiceConfiguration {

	@Value("${omc.service.name}")
	private String serviceName;

	@Value("${server.port:0}")
	private int serverPort;

	@Value("${omc.request.queue.max.size}")
	private int MaxRQSize;

	@Value("${omc.delivery.queue.max.size}")
	private int MaxDQSize;

	@Value("${omc.request.task.thread.size}")
	private int requestTaskThreadSize;

	@Value("${omc.delivery.task.thread.size}")
	private int deliveryTaskThreadSize;

	@Value("${zookeeper.host:}")
	private String zookeeperHost;

	@Value("${zookeeper.port:}")
	private String zookeeperPort;

	@Value("${omc.service.registry.mode:}")
	private String registryMode;

	@Value("${omc.service.discovery.mode:}")
	private String discoveryMode;

	@Value("${omc.collector.service.timestamp.regex}")
	private String timestampRegex;

	@Value("${omc.collector.service.timestamp.format}")
	private String timestampFormat;

	@Value("${omc.collector.service.quotecharacter}")
	private char quoteCharacter;

	@Value("${omc.collector.service.whitespace}")
	private char whiteSpace;

	private final Log logger = LogFactory.getLog(OmcCollectorServiceConfiguration.class);

	@Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return (container -> {
            container.setPort(omcObserverProperties().getServerPort());
        });
    }

	@Bean
    public OmcObserverProperties omcObserverProperties() {
		OmcObserverProperties omcObserverProperties = new OmcObserverProperties();
		omcObserverProperties.setServiceName(serviceName);
		omcObserverProperties.setHostName(OmcCommonUtil.getHostName());
		omcObserverProperties.setServerPort(OmcCommonUtil.getRandomServerPort(serverPort));
		omcObserverProperties.setObname();
		return omcObserverProperties;
    }

	@Bean
    public OmcObserverState omcObserverState() {
        return new OmcObserverState(omcObserverProperties().getObname(), MaxRQSize, MaxDQSize);
    }

	@Bean
    public BlockingQueue<String> requestQueue() {
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
    public OmcCollectorServiceManager omcCollectorServiceManager() {
		return new OmcCollectorServiceManager(timestampRegex, timestampFormat, quoteCharacter, whiteSpace, omcObserverProperties().getObname());
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