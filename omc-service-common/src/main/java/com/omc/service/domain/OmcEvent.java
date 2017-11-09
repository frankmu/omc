package com.omc.service.domain;

import java.util.List;
import java.util.Map;

public class OmcEvent {

	private String eventid;
	private List<Observer> observers;
	private Map<String, String> data;

	public String getEventid() {
		return eventid;
	}

	public List<Observer> getObservers() {
		return observers;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setEventid(String eventid) {
		this.eventid = eventid;
	}

	public void setObservers(List<Observer> observers) {
		this.observers = observers;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public class Observer {

		private String obname;
		private Integer intime;
		private Integer outtime;
		private ResponseState rs;

		public String getObname() {
			return obname;
		}
		public Integer getIntime() {
			return intime;
		}
		public Integer getOuttime() {
			return outtime;
		}
		public ResponseState getRs() {
			return rs;
		}
		public void setObname(String obname) {
			this.obname = obname;
		}
		public void setIntime(Integer intime) {
			this.intime = intime;
		}
		public void setOuttime(Integer outtime) {
			this.outtime = outtime;
		}
		public void setRs(ResponseState rs) {
			this.rs = rs;
		}
	}

	public enum ResponseState {
	    SUCCESS,
	    FAIL
	}
}