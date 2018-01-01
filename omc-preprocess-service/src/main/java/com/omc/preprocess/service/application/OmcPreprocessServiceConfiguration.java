package com.omc.preprocess.service.application;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.omc.geode.service.api.OmcAlertService;
import com.omc.geode.service.impl.OmcAlertServiceImpl;
import com.omc.preprocess.service.client.rule.OmcPreprocessServiceRules;
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
public class OmcPreprocessServiceConfiguration {

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

	@Value("${omc.preprocess.service.parser.classname}")
	private String parserClassname;

	@Value("${omc.geode.rest.url}")
	private String geodeRestUrl;

	@Value("${omc.geode.region.alert.origin}")
	private String geodeRegionAlertOrigin;

	@Value("${omc.geode.region.alert.detail}")
	private String geodeRegionAlertDetail;

	@Value("${omc.http.maxTotalConnections:200}")
	private int maxTotalConnections;

	@Value("${omc.http.maxDefaultConnectionsPerRoute:10}")
	private int maxDefaultConnectionsPerRoute;

	@Value("${omc.http.connection.request.timeout:0}")
	private int connectionRequestTimeout;

	@Value("${omc.http.connection.socket.timeout:0}")
	private int socketTimeout;

	@Value("${omc.http.connection.connect.timeout:0}")
	private int connectTimeout;

	@Autowired
	private ConfigurableApplicationContext ctx;

	private final Log logger = LogFactory.getLog(OmcPreprocessServiceConfiguration.class);

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
		} else if(DiscoveryMode.DNS.toString().equalsIgnoreCase(discoveryMode)) {
			return new OmcBaseServiceDiscovery();
		}
		return null;
    }

	@Bean
	public ThreadPoolTaskExecutor workerExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		int totalThreadSize = requestTaskThreadSize + deliveryTaskThreadSize;
		pool.setCorePoolSize(totalThreadSize);
		pool.setThreadNamePrefix("OMC-Worker-");
		logger.debug("Initialize Worker Executor with pool size: " + totalThreadSize);
		return pool;
	}

	@Bean
	public OmcPreprocessServiceRules omcPreprocessServiceRules() {
		try {
			return (OmcPreprocessServiceRules) Class.forName(parserClassname).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.error("No available parser class found in classpath. " + e.getMessage());
			ctx.close();
			return null;
		}
	}

	@Bean
	public RequestConfig requestConfig() {
		RequestConfig config = RequestConfig.custom()
				.setConnectionRequestTimeout(connectionRequestTimeout)
				.setConnectTimeout(connectTimeout)
				.setSocketTimeout(socketTimeout)
				.build();
		return config;
	}

	@Bean
	public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
		PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
		manager.setMaxTotal(maxTotalConnections);
		manager.setDefaultMaxPerRoute(maxDefaultConnectionsPerRoute);
		return manager;
	}

	@Bean
	public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
			RequestConfig requestConfig) {
		CloseableHttpClient client = HttpClientBuilder.create()
				.setConnectionManager(poolingHttpClientConnectionManager)
				.setDefaultRequestConfig(requestConfig)
				.build();
		return client;
	}

	@Bean
	public RestTemplate restTemplate(HttpClient httpClient) {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		return new RestTemplate(requestFactory);
	}

	@Bean
	public OmcAlertService omcAlertService(HttpClient httpClient) {
		return new OmcAlertServiceImpl(geodeRestUrl, geodeRegionAlertOrigin, geodeRegionAlertDetail, restTemplate(httpClient));
	}
}