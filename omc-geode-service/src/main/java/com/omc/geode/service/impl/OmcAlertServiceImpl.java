package com.omc.geode.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.omc.geode.service.api.OmcAlertService;
import com.omc.geode.service.domain.OmcGeodeServiceResult;
import com.omc.service.domain.OmcAlertDetail;
import com.omc.service.domain.OmcAlertOrigin;

public class OmcAlertServiceImpl extends OmcGeodeBaseService implements OmcAlertService {

	private final Log logger = LogFactory.getLog(OmcAlertServiceImpl.class);

	public OmcAlertServiceImpl(String url, String apiName, String apiVersion, RestTemplate restTemplate) {
		super(url, apiName, apiVersion, restTemplate);
	}

	@Override
	public OmcGeodeServiceResult createAlertOrigin(String key, OmcAlertOrigin omcAlertOrigin) {
		String url = getRegionRestUrl(this.alertOriginRegion) + key;
		logger.debug("Send post request to: " + url + " with data: " + omcAlertOrigin.toJson());
		ResponseEntity<String> response = this.restTemplate.postForEntity(url, omcAlertOrigin, String.class);
		return getOmcGeodeServiceResult(response);
	}

	@Override
	public OmcGeodeServiceResult createAlertDetails(String key, OmcAlertDetail omcAlertDetail) {
		String url = getRegionRestUrl(this.alertDetailRegion) + key;
		logger.debug("Send post request to: " + url + " with data: " + omcAlertDetail.toJson());
		ResponseEntity<String> response = this.restTemplate.postForEntity(url, omcAlertDetail, String.class);
		return getOmcGeodeServiceResult(response);
	}
}