package com.omc.service.domain;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class OmcAlertOrigin {

	private OmcEvent omcEvent;

	public OmcAlertOrigin(OmcEvent omcEvent) {
		this.omcEvent = omcEvent;
	}

	public String toJson(){
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("observers", this.omcEvent.getObservers());
		for (Map.Entry<String, Object> entry : this.omcEvent.getData().entrySet()) {
		    String key = entry.getKey();
		    if(key.startsWith(OmcEventConstant.STORE_PREFIX)) {
		    		json.put(key.replaceFirst(OmcEventConstant.STORE_PREFIX, ""), entry.getValue());
		    } else if(key.startsWith(OmcEventConstant.SYSTEM_PREFIX)) {
		    		json.put(key.replaceFirst(OmcEventConstant.SYSTEM_PREFIX, ""), entry.getValue());
		    }
		}
	    return new Gson().toJson(json);
	}
}
