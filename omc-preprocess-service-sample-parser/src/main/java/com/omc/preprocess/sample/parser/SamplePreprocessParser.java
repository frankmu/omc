package com.omc.preprocess.sample.parser;

import java.util.HashMap;
import java.util.Map;

import com.omc.preprocess.service.client.rule.OmcPreprocessServiceRules;

public class SamplePreprocessParser implements OmcPreprocessServiceRules {

	@Override
	public Map<String, Object> parse(Map<String, Object> data) {
		Map<String, Object> parsedData = new HashMap<String, Object>(data);
		parsedData.put("@agent", "Test Agent");
		parsedData.put("@class", "Syslog");
		parsedData.put("@summary", (String)data.get("$token1") + " " +  (String)data.get("$token2"));
		parsedData.put("@occurrence", "1509686567");
		parsedData.put("@identifier", "Syslog_Collector_test_36000 for event abc");
		parsedData.put("system_dupfield", "summary, severity");
		return parsedData;
	}
}