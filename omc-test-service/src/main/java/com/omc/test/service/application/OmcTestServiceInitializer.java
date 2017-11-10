package com.omc.test.service.application;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.omc.service.registration.OmcServiceRegistry;

@Component
public class OmcTestServiceInitializer {

	@Value("${omc.service.registry.name}")
	private String omcServiceRegistryName;

	@Autowired
	OmcServiceRegistry omcServiceRegistry;
	
	@PostConstruct
    public void init() {
       omcServiceRegistry.registerService(omcServiceRegistryName, "127.0.0.1:8000");
    }

	@PreDestroy
	public void destroy() {
		omcServiceRegistry.unregisterService(omcServiceRegistryName, "127.0.0.1:8000");
	}
}