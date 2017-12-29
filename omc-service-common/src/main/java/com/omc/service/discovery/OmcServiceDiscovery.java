package com.omc.service.discovery;

public interface OmcServiceDiscovery {

	public enum DiscoveryMode {
	    ZOOKEEPER,
	    DNS,
	    URL
	}

    public String discoverServiceURI(String name);
}