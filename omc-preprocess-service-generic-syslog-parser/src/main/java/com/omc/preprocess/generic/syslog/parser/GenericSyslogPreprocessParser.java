package com.omc.preprocess.generic.syslog.parser;

import java.util.HashMap;
import java.util.Map;

import com.omc.preprocess.service.client.rule.OmcPreprocessServiceRules;

public class GenericSyslogPreprocessParser implements OmcPreprocessServiceRules {

	@Override
	public Map<String, Object> parse(Map<String, Object> data) {
		Map<String, Object> parsedData = new HashMap<String, Object>(data);
		parsedData.put("@agent", (String)data.get("$agent"));
		parsedData.put("@class", "Generic Syslog");
//		get rid of the first date/time string in $token0 before put to summary and identifier 
		String str1 = ((String)data.get("$token0")).replaceAll("^[\\w ]+[\\d]+ [\\d]{2}:[\\d]{2}:[\\d]{2} ", "");
//		parsedData.put("@summary", (String)data.get("$token0"));
		parsedData.put("@summary", str1);
		parsedData.put("@occurrence", data.get("$time"));
		parsedData.put("@identifier", (String)data.get("$agent") + " " + str1);
		parsedData.put("system_dupfield", "agent, summary");
		return parsedData;
	}
}