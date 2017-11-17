package com.omc.service.exception;

public class OmcRequestQueueFullException extends Exception {

	private static final long serialVersionUID = -3139178673350438873L;

	public OmcRequestQueueFullException(String message) {
        super(message);
    }
}