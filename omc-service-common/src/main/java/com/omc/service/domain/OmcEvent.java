package com.omc.service.domain;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class OmcEvent {

	private String eventid;
	private List<OmcObserver> observers;
	private Map<String, String> data;

	public String getEventid() {
		return eventid;
	}

	public List<OmcObserver> getObservers() {
		return observers;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setEventid(String eventid) {
		this.eventid = eventid;
	}

	public void setObservers(List<OmcObserver> observers) {
		this.observers = observers;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	@Override
	public String toString(){
	    return new Gson().toJson(this);
	}
}