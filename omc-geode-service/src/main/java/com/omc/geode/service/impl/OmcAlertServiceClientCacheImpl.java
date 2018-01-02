package com.omc.geode.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;

import com.omc.geode.service.api.OmcAlertService;
import com.omc.geode.service.domain.OmcGeodeServiceResult;
import com.omc.service.domain.OmcAlertDetail;
import com.omc.service.domain.OmcAlertOrigin;

public class OmcAlertServiceClientCacheImpl extends OmcGeodeBaseService implements OmcAlertService {

	private final Log logger = LogFactory.getLog(OmcAlertServiceClientCacheImpl.class);

	private ClientCache clientCache;

	public OmcAlertServiceClientCacheImpl(ClientCache clientCache, String alertOriginRegion, String alertDetailRegion) {
		super(alertOriginRegion, alertDetailRegion);
		this.clientCache = clientCache;
	}

	@Override
	public OmcGeodeServiceResult createAlertOrigin(String key, OmcAlertOrigin omcAlertOrigin) {
		Region<String, Object> region = clientCache.getRegion(this.alertOriginRegion);
		PdxInstance instane = JSONFormatter.fromJSON(omcAlertOrigin.toJson());
		region.put(key, instane);
		logger.debug("Insert into " + this.alertOriginRegion + " with key: [" + key + "] data: " + omcAlertOrigin.toJson());
		return new OmcGeodeServiceResult(true);
	}

	@Override
	public OmcGeodeServiceResult createAlertDetail(String key, OmcAlertDetail omcAlertDetail) {
		Region<String, Object> region = clientCache.getRegion(this.alertDetailRegion);
		PdxInstance instane = JSONFormatter.fromJSON(omcAlertDetail.toJson());
		region.put(key, instane);
		logger.debug("Insert into " + this.alertDetailRegion + " with key: [" + key + "] data: " + omcAlertDetail.toJson());
		return new OmcGeodeServiceResult(true);
	}
}