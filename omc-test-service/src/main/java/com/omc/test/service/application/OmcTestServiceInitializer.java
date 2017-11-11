package com.omc.test.service.application;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.omc.service.registration.OmcServiceRegistry;

@Component
public class OmcTestServiceInitializer {

	@Value("${server.port}")
	private String serverPort;

	@Value("${omc.service.registry.name}")
	private String omcServiceRegistryName;

	@Autowired
	OmcServiceRegistry omcServiceRegistry;
	
	@Autowired
    private ServletContext servletContext;

	@PostConstruct
	public void init() throws UnknownHostException {
		String ip = InetAddress.getLocalHost().getHostAddress();
		omcServiceRegistry.registerService(omcServiceRegistryName, ip + "/" + serverPort + servletContext.getContextPath());
	}

	@PreDestroy
	public void destroy() throws UnknownHostException {
		String ip = InetAddress.getLocalHost().getHostAddress();
		omcServiceRegistry.unregisterService(omcServiceRegistryName, ip + "/" + serverPort + servletContext.getContextPath());
	}
}