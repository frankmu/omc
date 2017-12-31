package com.omc.collector.service.application;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.web.client.AsyncRestTemplate;

import com.omc.collector.service.processor.OmcCollectorServiceManager;
import com.omc.service.discovery.OmcBaseServiceDiscovery;
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

	@Value("${omc.delivery.nio.thread.size:1}")
	private int deliveryNIOThreadSize;

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
	private String quoteCharacter;

	@Value("${omc.collector.service.whitespace}")
	private String whiteSpace;

    @Value("${omc.http.maxTotalConnections:20}")
    private int maxTotalConnections;

    @Value("${omc.http.maxConnectionsPerRoute:2}")
    private int maxConnectionsPerRoute;

    @Value("${omc.http.connection.request.timeout:10000}")
    private int connectionRequestTimeout;

    @Value("${omc.http.connection.socket.timeout:2000}")
    private int socketTimeout;

    @Value("${omc.http.connection.connect.timeout:2000}")
    private int connectTimeout;

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
		} else if(DiscoveryMode.DNS.toString().equalsIgnoreCase(discoveryMode) || DiscoveryMode.URL.toString().equalsIgnoreCase(discoveryMode)) {
			return new OmcBaseServiceDiscovery();
		}
		return null;
    }

	@Bean
    public OmcCollectorServiceManager omcCollectorServiceManager() {
		char delimiter;
		char quote;
		// Only accept single char white space delimiter
		Assert.isTrue(whiteSpace != null && (whiteSpace.length() == 1 || whiteSpace.length() == 3), "White space charater is not valid.");
		Assert.isTrue(quoteCharacter != null && (quoteCharacter.length() == 1 || quoteCharacter.length() == 3), "Quote charater is not valid.");
		if(whiteSpace.length() == 1) {
			delimiter = whiteSpace.charAt(0);
		} else {
			delimiter = whiteSpace.charAt(1);
		}
		if(quoteCharacter.length() == 1) {
			quote = quoteCharacter.charAt(0);
		} else {
			quote = quoteCharacter.charAt(1);
		}
		return new OmcCollectorServiceManager(timestampRegex, timestampFormat, quote, delimiter, omcObserverProperties().getObname());
    }

	@Bean
	public ThreadPoolTaskExecutor workerExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		// add 1 for read standard input from shell command
		int totalThreadSize = requestTaskThreadSize + deliveryTaskThreadSize + 1;
		pool.setCorePoolSize(totalThreadSize);
		pool.setThreadNamePrefix("OMC-Worker-");
		logger.debug("Initialize Worker Executor with pool size: " + totalThreadSize);
		return pool;
	}

	@Bean
	public ThreadPoolTaskExecutor nioWorkerExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(deliveryNIOThreadSize);
		pool.setThreadNamePrefix("OMC-NIO-");
		return pool;
	}

    @Bean
    public AsyncClientHttpRequestFactory clientHttpRequestFactory(CloseableHttpAsyncClient closeableHttpAsyncClient) {
		HttpComponentsAsyncClientHttpRequestFactory factory = new HttpComponentsAsyncClientHttpRequestFactory(closeableHttpAsyncClient);
		return factory;
    }

    @Bean
    public CloseableHttpAsyncClient asyncHttpClient(PoolingNHttpClientConnectionManager poolingNHttpClientConnectionManager) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();

        return HttpAsyncClientBuilder
                .create()
                .setConnectionManager(poolingNHttpClientConnectionManager)
                .setDefaultRequestConfig(config).build();
    }

    @Bean
    PoolingNHttpClientConnectionManager poolingNHttpClientConnectionManager() throws IOReactorException {
        PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(
                new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT, nioWorkerExecutor()));
        connectionManager.setMaxTotal(maxTotalConnections);
        connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
        return connectionManager;
    }

	@Bean
	public AsyncRestTemplate restTemplate(AsyncClientHttpRequestFactory asyncClientHttpRequestFactory) {
		return new AsyncRestTemplate(asyncClientHttpRequestFactory);
	}
}