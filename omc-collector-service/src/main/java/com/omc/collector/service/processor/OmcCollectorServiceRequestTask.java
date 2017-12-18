package com.omc.collector.service.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.omc.collector.service.util.OmcCollectorServiceUtils;
import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcEventConstant;
import com.omc.service.domain.OmcObserver;
import com.omc.service.domain.OmcObserverState;
import com.omc.service.util.OmcEventUtil;

public class OmcCollectorServiceRequestTask implements Runnable{
	
	private BlockingQueue<String> requestQueue;
	private BlockingQueue<OmcEvent> deliveryQueue;
	private OmcCollectorServiceManager omcCollectorServiceManager;
	private OmcObserverState omcObserverState;

	private final Log logger = LogFactory.getLog(OmcCollectorServiceRequestTask.class);

	public OmcCollectorServiceRequestTask(BlockingQueue<String> requestQueue, BlockingQueue<OmcEvent> deliveryQueue, OmcCollectorServiceManager omcCollectorServiceManager, OmcObserverState omcObserverState) {
		this.requestQueue = requestQueue;
		this.deliveryQueue = deliveryQueue;
		this.omcCollectorServiceManager = omcCollectorServiceManager;
		this.omcObserverState = omcObserverState;
	}

	@Override
	public void run() {
		logger.debug("Starting Request Queue worker thread");
		while (true) {
			try {
				String message = this.requestQueue.take();
				logger.debug("Get task from Request Queue: " + message);
				OmcEvent result = process(message);
				if(result != null) {
					deliveryQueue.put(result);
				}
			} catch (InterruptedException e) {
				logger.error("Thread was interrupted!");
				break;
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	private OmcEvent process(String message) {
		Pattern p = Pattern.compile(this.omcCollectorServiceManager.getTimestampRegex());
		Matcher m = p.matcher(message);
		if (m.find()) {
			String timestamp = m.group(0);
			String targetMessage = message.replaceAll(timestamp, "").trim();
			List<String> tokens = new ArrayList<String>();
			int start = 0;
			boolean inQuotes = false;
			for (int current = 0; current < targetMessage.length(); current++) {
				if (targetMessage.charAt(current) == this.omcCollectorServiceManager.getQuoteCharacter()) {
					inQuotes = !inQuotes;
				}
				boolean atLastChar = (current == targetMessage.length() - 1);
				if (atLastChar) {
					String s = targetMessage.substring(start).replaceAll(Character.toString(this.omcCollectorServiceManager.getQuoteCharacter()), "");
					if(s.length() > 0) {
						tokens.add(s);
					}
				} else if (targetMessage.charAt(current) == this.omcCollectorServiceManager.getWhiteSpace() && !inQuotes) {
					String s = targetMessage.substring(start, current).replaceAll(Character.toString(this.omcCollectorServiceManager.getQuoteCharacter()), "");
					if(s.length() > 0) {
						tokens.add(s);
					}
					start = current + 1;
				}
			}

			// Create the OmcEvent object
			OmcEvent omcEvent = new OmcEvent();
			omcEvent.setEventid(OmcCollectorServiceUtils.generateEventId(this.omcCollectorServiceManager.getObname()));
			omcEvent.setObservers(new ArrayList<OmcObserver>());
			Map<String, Object> eventData = new HashMap<String, Object>();
			eventData.put(OmcEventConstant.EVENT_AGENT, this.omcCollectorServiceManager.getObname());
			eventData.put(OmcEventConstant.EVENT_CLASS, "Syslog");
			eventData.put(OmcEventConstant.EVENT_COUNT, tokens.size() + 1);
			eventData.put(OmcEventConstant.EVENT_TIME, OmcCollectorServiceUtils.getFormattedTimestamp(timestamp, this.omcCollectorServiceManager.getTimestampFormat()));
			eventData.put(OmcEventConstant.EVENT_TOKEN_PREFIX + "0", message);
			for(int i = 1; i <= tokens.size(); i++) {
				eventData.put(OmcEventConstant.EVENT_TOKEN_PREFIX + i, tokens.get(i - 1));
			}
			omcEvent.setData(eventData);
			OmcEventUtil.appendCurrentObserver(omcEvent, omcObserverState.getObname());
			logger.debug("Create OmcEvent: " + omcEvent.toString());
			return omcEvent;
		} else {
			logger.error("Cannot extract timestamp from: " + message + " using regex: " + this.omcCollectorServiceManager.getTimestampRegex());
			return null;
		}
	}
}