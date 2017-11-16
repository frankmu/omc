package com.omc.service.registration;

public interface OmcServiceRegistry {

	public enum RegistryMode {
	    ZOOKEEPER,
	    MESOS
	}

	public void registerService(String name, String uri);
    
    public void unregisterService(String name, String uri);
}