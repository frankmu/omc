package com.omc.geode.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.omc.geode.service.api.OmcAlertService;
import com.omc.geode.service.domain.OmcGeodeServiceResult;
import com.omc.service.domain.OmcAlertDetail;
import com.omc.service.domain.OmcAlertOrigin;

public class OmcAlertServiceImpl extends OmcGeodeBaseService implements OmcAlertService {

	private final Log logger = LogFactory.getLog(OmcAlertServiceImpl.class);

	public OmcAlertServiceImpl(String restUrl, String alertOriginRegion, String alertDetailRegion, RestTemplate restTemplate) {
		super(restUrl, alertOriginRegion, alertDetailRegion, restTemplate);
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
}