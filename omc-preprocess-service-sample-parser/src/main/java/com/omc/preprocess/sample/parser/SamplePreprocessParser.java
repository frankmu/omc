package com.omc.preprocess.sample.parser;

import java.util.HashMap;
import java.util.Map;

import com.omc.preprocess.service.client.rule.OmcPreprocessServiceRules;

public class SamplePreprocessParser implements OmcPreprocessServiceRules {

	@Override
	public Map<String, Object> parse(Map<String, Object> data) {
		Map<String, Object> parsedData = new HashMap<String, Object>(data);
		parsedData.put("store_summary", (String)data.get("event_token1") + (String)data.get("event_token2"));
		return parsedData;
	}
}