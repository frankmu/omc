package com.omc.collector.service.processor;

public class OmcCollectorServiceManager {
	private String obname;
	private String timestampRegex;
	private String timestampFormat;
	private Character quoteCharacter;
	private Character whiteSpace;

	public OmcCollectorServiceManager(String timestampRegex, String timestampFormat, char quoteCharacter, char whiteSpace, String obname) {
		this.timestampFormat = timestampFormat;
		this.timestampRegex = timestampRegex;
		this.quoteCharacter = quoteCharacter;
		this.whiteSpace = whiteSpace;
		this.obname = obname;
	}

	public String getTimestampRegex() {
		return timestampRegex;
	}
	public String getTimestampFormat() {
		return timestampFormat;
	}
	public Character getQuoteCharacter() {
		return quoteCharacter;
	}
	public Character getWhiteSpace() {
		return whiteSpace;
	}
	public String getObname() {
		return obname;
	}
	public void setTimestampRegex(String timestampRegex) {
		this.timestampRegex = timestampRegex;
	}
	public void setTimestampFormat(String timestampFormat) {
		this.timestampFormat = timestampFormat;
	}
	public void setQuoteCharacter(Character quoteCharacter) {
		this.quoteCharacter = quoteCharacter;
	}
	public void setWhiteSpace(Character whiteSpace) {
		this.whiteSpace = whiteSpace;
	}
	public void setObname(String obname) {
		this.obname = obname;
	}
}
