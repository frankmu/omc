package com.omc.test.service.application;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.omc.service.domain.OmcEvent;
import com.omc.service.management.processor.OmcManagementDefaultProcessor;
import com.omc.service.management.processor.OmcManagementProcessor;
import com.omc.service.registration.OmcServiceRegistry;
import com.omc.service.zookeeper.OmcZookeeperServiceRegistry;

@Configuration
public class OmcTestServiceConfiguration {

	@Value("${zookeeper.host}")
	private String zookeeperHost;

	@Value("${zookeeper.port}")
	private String zookeeperPort;

	@Bean
    public OmcManagementProcessor omcManagementProcessor() {
        return new OmcManagementDefaultProcessor();
    }

	@Bean
    public BlockingQueue<OmcEvent> requestQueue() {
		OmcManagementProcessor managementProcessor = omcManagementProcessor();
        return new LinkedBlockingQueue<>(managementProcessor.getRequestQueueSize());
    }

	@Bean
    public BlockingQueue<OmcEvent> deliveryQueue() {
		OmcManagementProcessor managementProcessor = omcManagementProcessor();
        return new LinkedBlockingQueue<>(managementProcessor.getDeliveryQueueSize());
    }

	@Bean
    public OmcServiceRegistry omcServiceRegistry() {
        return new OmcZookeeperServiceRegistry(zookeeperHost, zookeeperPort);
    }

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		OmcManagementProcessor managementProcessor = omcManagementProcessor();
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(managementProcessor.getTaskExecutorCorePoolSize());
		pool.setMaxPoolSize(managementProcessor.getTaskExecutorMaxPoolSize());
		pool.setWaitForTasksToCompleteOnShutdown(true);
		return pool;
	}
}