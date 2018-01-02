package com.omc.geode.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.omc.geode.service.api.OmcAlertService;
import com.omc.geode.service.domain.OmcGeodeServiceResult;
import com.omc.service.domain.OmcAlertDetail;
import com.omc.service.domain.OmcAlertOrigin;

public class OmcAlertServiceRestImpl extends OmcGeodeBaseService implements OmcAlertService {

	private final Log logger = LogFactory.getLog(OmcAlertServiceRestImpl.class);
	private static final String KEY = "key";

	private String restUrl;
	private RestTemplate restTemplate;
	private HttpHeaders headers;

	public OmcAlertServiceRestImpl(String restUrl, String alertOriginRegion, String alertDetailRegion, RestTemplate restTemplate) {
		super(alertOriginRegion, alertDetailRegion);
		this.restUrl = restUrl;
		this.restTemplate = restTemplate;
		this.headers = new HttpHeaders();
		this.headers.setContentType(MediaType.APPLICATION_JSON);
	}

	@Override
	public OmcGeodeServiceResult createAlertOrigin(String key, OmcAlertOrigin omcAlertOrigin) {
		String url = getRegionRestUrl(this.alertOriginRegion) + key;
		logger.debug("Send post request to: " + url + " with data: " + omcAlertOrigin.toJson());
		HttpEntity<String> entity = new HttpEntity<String>(omcAlertOrigin.toJson(), this.headers);
		ResponseEntity<String> response = this.restTemplate.postForEntity(url, entity, String.class);
		return getOmcGeodeServiceResult(response);
	}

	@Override
	public OmcGeodeServiceResult createAlertDetail(String key, OmcAlertDetail omcAlertDetail) {
		String url = getRegionRestUrl(this.alertDetailRegion) + key;
		logger.debug("Send post request to: " + url + " with data: " + omcAlertDetail.toJson());
		HttpEntity<String> entity = new HttpEntity<String>(omcAlertDetail.toJson(), this.headers);
		ResponseEntity<String> response = this.restTemplate.postForEntity(url, entity, String.class);
		return getOmcGeodeServiceResult(response);
	}

	private String getRegionRestUrl(String regionName) {
		return this.restUrl + regionName + "?" + KEY + "=";
	}

	private OmcGeodeServiceResult getOmcGeodeServiceResult(ResponseEntity<String> response) {
		HttpStatus status = response.getStatusCode();
		if(HttpStatus.CREATED.equals(status)) {
			return new OmcGeodeServiceResult(true);
		}else {
			logger.error("Error creating the record with status code: " + status + " message: " + response.getBody());
			return new OmcGeodeServiceResult(status.toString(), response.getBody());
		}
	}
}