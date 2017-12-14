package com.omc.geode.service.domain;

import java.util.Map;

public class OmcGeodeServiceResult {
	private boolean isSuccessful;
	private String errorCode;
	private String errorMessage;
	private Map<String, Object> payload;

	public OmcGeodeServiceResult(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

	public OmcGeodeServiceResult(String errorCode, String errorMessage) {
		this.isSuccessful = false;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public boolean isSuccessful() {
		return isSuccessful;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public Map<String, Object> getPayload() {
		return payload;
	}

	public void setSuccessful(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setPayload(Map<String, Object> payload) {
		this.payload = payload;
	}
}
