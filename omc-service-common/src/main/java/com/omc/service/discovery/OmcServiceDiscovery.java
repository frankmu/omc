package com.omc.service.discovery;

public interface OmcServiceDiscovery {

	public enum DiscoveryMode {
	    ZOOKEEPER,
	    DNS
	}

    public String discoverServiceURI(String name);
}