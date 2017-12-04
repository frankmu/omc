package com.omc.service.discovery;

public class OmcDNSServiceDiscovery implements OmcServiceDiscovery {

	@Override
	public String discoverServiceURI(String host) {
		return host;
	}
}
