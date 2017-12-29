package com.omc.service.discovery;

public class OmcBaseServiceDiscovery implements OmcServiceDiscovery {

	@Override
	public String discoverServiceURI(String host) {
		return host;
	}
}
