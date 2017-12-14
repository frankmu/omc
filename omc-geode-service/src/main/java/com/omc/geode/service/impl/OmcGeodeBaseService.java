package com.omc.geode.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.omc.geode.service.domain.OmcGeodeServiceResult;

public abstract class OmcGeodeBaseService {

	static final String KEY = "key";

	private final Log logger = LogFactory.getLog(OmcGeodeBaseService.class);

	String restUrl;
	String alertOriginRegion;
	String alertDetailRegion;
	RestTemplate restTemplate;

	public OmcGeodeBaseService(String restUrl, String alertOriginRegion, String alertDetailRegion, RestTemplate restTemplate) {
		this.restUrl = restUrl;
		this.alertOriginRegion = alertOriginRegion;
		this.alertDetailRegion = alertDetailRegion;
		this.restTemplate = restTemplate;
	}

	String getRegionRestUrl(String regionName) {
		return this.restUrl + regionName + "?" + KEY + "=";
	}

	OmcGeodeServiceResult getOmcGeodeServiceResult(ResponseEntity<String> response) {
		HttpStatus status = response.getStatusCode();
		if(HttpStatus.CREATED.equals(status)) {
			return new OmcGeodeServiceResult(true);
		}else {
			logger.error("Error creating the record with status code: " + status + " message: " + response.getBody());
			return new OmcGeodeServiceResult(status.toString(), response.getBody());
		}
	}
}
