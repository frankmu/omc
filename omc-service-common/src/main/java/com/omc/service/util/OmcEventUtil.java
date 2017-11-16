package com.omc.service.util;

import java.util.ArrayList;
import java.util.List;

import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcObserver;

public class OmcEventUtil {

	public static void appendCurrentObserver(OmcEvent omcEvent, String observerName) {
		List<OmcObserver> observers = omcEvent.getObservers();
		if(observers == null) {
			observers = new ArrayList<OmcObserver>();
		}
		OmcObserver newObserver = new OmcObserver();
		newObserver.setObname(observerName);
		newObserver.setIntime(System.currentTimeMillis());
		observers.add(newObserver);
		omcEvent.setObservers(observers);
	}
}