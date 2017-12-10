package com.omc.preprocess.service.client.rule;

import java.util.Map;

/**
 * Public interface for parse data from collector service
 * 
 * @param data Original data map from collector service
 * 	e.g. {
 * 			"event_agent": "Syslog_Collector",
 * 			"event_class": "Syslog",
 * 			"event_token0": "Jun  9 09:55:05 192.168.0.131 263030: *Jun  9 10:16:22.732: xxx.xxx.xxx.xx %SNMP-3-AUTHFAIL: Authentication failure for SNMP req from host 192.16.1.18",
 * 			"event_token1": "192.168.0.131",
 * 			 ...
 * 			"event_count": 15,
 * 			"event_time": 1509686567
 * 			
 * 		 }
 * 
 * @return map which contains all the concatenated tokens, notice the key should start with store_[database field name] prefix if want to persist in db
 * 	e.g. {
 * 			"store_summary": "concatenate string from different event tokens"
 * 		}
 */
public interface OmcPreprocessServiceRules {
	public Map<String, Object> parse(Map<String, Object> data);
}
