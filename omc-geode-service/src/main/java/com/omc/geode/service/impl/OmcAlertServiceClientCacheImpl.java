package com.omc.geode.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;

import com.omc.geode.service.api.OmcAlertService;
import com.omc.geode.service.domain.OmcGeodeServiceResult;
import com.omc.service.domain.OmcAlertDetail;
import com.omc.service.domain.OmcAlertOrigin;

public class OmcAlertServiceClientCacheImpl implements OmcAlertService {

	private final Log logger = LogFactory.getLog(OmcAlertServiceClientCacheImpl.class);

	private Region<String, Object> originRegion;
	private Region<String, Object> detailRegion;

	public OmcAlertServiceClientCacheImpl(Region<String, Object> originRegion, Region<String, Object> detailRegion) {
		this.originRegion = originRegion;
		this.detailRegion = detailRegion;
	}

	@Override
	public OmcGeodeServiceResult createAlertOrigin(String key, OmcAlertOrigin omcAlertOrigin) {
		PdxInstance instane = JSONFormatter.fromJSON(omcAlertOrigin.toJson());
		originRegion.put(key, instane);
		logger.debug("Insert into " + this.originRegion.getName() + " with key: [" + key + "] data: " + omcAlertOrigin.toJson());
		return new OmcGeodeServiceResult(true);
	}

	@Override
	public OmcGeodeServiceResult createAlertDetail(String key, OmcAlertDetail omcAlertDetail) {
		PdxInstance instane = JSONFormatter.fromJSON(omcAlertDetail.toJson());
		detailRegion.put(key, instane);
		logger.debug("Insert into " + this.detailRegion.getName() + " with key: [" + key + "] data: " + omcAlertDetail.toJson());
		return new OmcGeodeServiceResult(true);
	}
}