package com.omc.service.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OmcAlertDetail {

	private OmcEvent omcEvent;

	public OmcAlertDetail(OmcEvent omcEvent) {
		this.omcEvent = omcEvent;
	}

	public String toJson(){
		String eventPrefix = Pattern.quote(OmcEventConstant.EVENT_PREFIX);
		Map<String, Object> json = new HashMap<String, Object>();
		Map<String, Object> data = this.omcEvent.getData();
		json.put("agent", data.get(OmcEventConstant.EVENT_AGENT));
		json.put("class", data.get(OmcEventConstant.EVENT_CLASS));
		json.put("count", data.get(OmcEventConstant.EVENT_COUNT));
		json.put("time", data.get(OmcEventConstant.EVENT_TIME));
		for (Map.Entry<String, Object> entry : this.omcEvent.getData().entrySet()) {
		    String key = entry.getKey();
		    if(key.startsWith(OmcEventConstant.EVENT_TOKEN_PREFIX)) {
		    		json.put(key.replaceFirst(eventPrefix, ""), entry.getValue());
		    }
		}
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	    return gson.toJson(json);
	}
}
