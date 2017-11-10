package com.omc.service.registration;

public interface OmcServiceRegistry {

	public void registerService(String name, String uri);
    
    public void unregisterService(String name, String uri);
    
    public String discoverServiceURI(String name);
}