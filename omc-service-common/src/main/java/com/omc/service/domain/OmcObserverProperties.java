package com.omc.service.domain;

public class OmcObserverProperties {
	private String hostName;
	private int serverPort;
	private String serviceName;
	private String obname;

	public String getObname() {
		return obname;
	}
	public String getHostName() {
		return hostName;
	}
	public int getServerPort() {
		return serverPort;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public void setObname() {
		this.obname = this.serviceName + "-" + this.hostName + "-" + this.serverPort;
	}
	public void setObname(String obname) {
		this.obname = obname;
	}
}
