package com.xassure.reporting.beans;

public class Locators {

	private String runId;
	private String pageName;
	private String locatorTime;
	private String elementName;
	private String date;
	private String time;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRunId() {
		return runId;
	}

	public void setRunId(String runId) {
		this.runId = runId;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getLocatorTime() {
		return locatorTime;
	}

	public void setLocatorTime(String locatorTime) {
		this.locatorTime = locatorTime;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

}
