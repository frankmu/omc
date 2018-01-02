package com.omc.geode.service.impl;

public abstract class OmcGeodeBaseService {

	String alertOriginRegion;
	String alertDetailRegion;

	public OmcGeodeBaseService(String alertOriginRegion, String alertDetailRegion) {
		this.alertOriginRegion = alertOriginRegion;
		this.alertDetailRegion = alertDetailRegion;
	}
}
