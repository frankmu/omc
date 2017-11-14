package com.omc.test.service.application;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcObserverState;
import com.omc.service.registration.OmcServiceRegistry;
import com.omc.service.zookeeper.OmcZookeeperServiceRegistry;

@Configuration
public class OmcTestServiceConfiguration {

	@Value("${omc.obname}")
	private String obname;

	@Value("${omc.request.queue.max.size}")
	private int MaxRQSize;

	@Value("${omc.delivery.queue.max.size}")
	private int MaxDQSize;

	@Value("${omc.task.thread.executor.core.pool.size}")
	private int taskExecutorCorePoolSize;

	@Value("${omc.task.thread.executor.max.pool.size}")
	private int taskExecutorMaxPoolSize;

	@Value("${zookeeper.host}")
	private String zookeeperHost;

	@Value("${zookeeper.port}")
	private String zookeeperPort;

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
    public OmcServiceRegistry omcServiceRegistry() {
        return new OmcZookeeperServiceRegistry(zookeeperHost, zookeeperPort);
    }

	@Bean
    public AsyncTaskExecutor managerExecutor() {
        SimpleAsyncTaskExecutor pool = new SimpleAsyncTaskExecutor();
        pool.setConcurrencyLimit(2);
        pool.setThreadNamePrefix("OMC-Manager-");
        logger.debug("Initialize Manager Executor");
        return pool;
    }

	@Bean
	public ThreadPoolTaskExecutor workerExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(taskExecutorCorePoolSize);
		pool.setMaxPoolSize(taskExecutorMaxPoolSize);
		pool.setWaitForTasksToCompleteOnShutdown(true);
		pool.setThreadNamePrefix("OMC-Worker-");
		logger.debug("Initialize Worker Executor with core pool size: " + taskExecutorCorePoolSize + ", max pool size: " + taskExecutorMaxPoolSize);
		return pool;
	}
}