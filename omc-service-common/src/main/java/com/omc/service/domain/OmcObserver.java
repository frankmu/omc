package com.omc.service.domain;

public class OmcObserver {

	public enum ResponseState {
	    SUCCESS,
	    FAIL
	}

	private String obname;
	private Long intime;
	private Long outtime;
	private ResponseState rs;

	public String getObname() {
		return obname;
	}
	public Long getIntime() {
		return intime;
	}
	public Long getOuttime() {
		return outtime;
	}
	public ResponseState getRs() {
		return rs;
	}
	public void setObname(String obname) {
		this.obname = obname;
	}
	public void setIntime(Long intime) {
		this.intime = intime;
	}
	public void setOuttime(Long outtime) {
		this.outtime = outtime;
	}
	public void setRs(ResponseState rs) {
		this.rs = rs;
	}
}
