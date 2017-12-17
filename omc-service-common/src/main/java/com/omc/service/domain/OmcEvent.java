package com.omc.service.domain;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OmcEvent {

	@NotBlank(message = "eventid cannot be empty!")
	private String eventid;

	@NotNull(message = "observers cannot be null!")
	private List<OmcObserver> observers;

	@NotEmpty(message = "data cannot be empty!")
	private Map<String, Object> data;

	public String getEventid() {
		return eventid;
	}

	public List<OmcObserver> getObservers() {
		return observers;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setEventid(String eventid) {
		this.eventid = eventid;
	}

	public void setObservers(List<OmcObserver> observers) {
		this.observers = observers;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	@Override
	public String toString(){
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	    return gson.toJson(this);
	}
}