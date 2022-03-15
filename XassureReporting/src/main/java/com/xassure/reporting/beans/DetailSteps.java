package com.xassure.reporting.beans;

import java.util.Objects;

public class DetailSteps {

    private String runId;
    private String module;

    private String testCaseName;
    private String pageName;
    private String testStep;
    private String testStepStatus;
    private String browserName;
    private String pageLoadTime;
    private String apiResponseTime;
    private String remarks;
    private String failureId;
    private String screenshotId;
    private String executionDate;
    private String executionTime;
    private String locale;
    private String lang;
    private String description;
    private String os;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetailSteps)) return false;
        DetailSteps that = (DetailSteps) o;
        return Objects.equals(getRunId(), that.getRunId()) &&
                Objects.equals(getModule(), that.getModule()) &&
                Objects.equals(getTestCaseName(), that.getTestCaseName()) &&
                Objects.equals(getPageName(), that.getPageName()) &&
                Objects.equals(getTestStep(), that.getTestStep()) &&
                Objects.equals(getTestStepStatus(), that.getTestStepStatus()) &&
                Objects.equals(getBrowserName(), that.getBrowserName()) &&
                Objects.equals(getPageLoadTime(), that.getPageLoadTime()) &&
                Objects.equals(getApiResponseTime(), that.getApiResponseTime()) &&
                Objects.equals(getRemarks(), that.getRemarks()) &&
                Objects.equals(getFailureId(), that.getFailureId()) &&
                Objects.equals(getScreenshotId(), that.getScreenshotId()) &&
                Objects.equals(getExecutionDate(), that.getExecutionDate()) &&
                Objects.equals(getExecutionTime(), that.getExecutionTime()) &&
                Objects.equals(getLocale(), that.getLocale()) &&
                Objects.equals(getLang(), that.getLang());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRunId(), getModule(), getTestCaseName(), getPageName(), getTestStep(), getTestStepStatus(), getBrowserName(), getPageLoadTime(), getApiResponseTime(), getRemarks(), getFailureId(), getScreenshotId(), getExecutionDate(), getExecutionTime(), getLocale(), getLang());
    }


    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getTestStep() {
        return testStep;
    }

    public void setTestStep(String testStep) {
        this.testStep = testStep;
    }

    public String getTestStepStatus() {
        return testStepStatus;
    }

    public void setTestStepStatus(String testStepStatus) {
        this.testStepStatus = testStepStatus;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getPageLoadTime() {
        return pageLoadTime;
    }

    public void setPageLoadTime(String pageLoadTime) {
        this.pageLoadTime = pageLoadTime;
    }

    public String getApiResponseTime() {
        return apiResponseTime;
    }

    public void setApiResponseTime(String apiResponseTime) {
        this.apiResponseTime = apiResponseTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getFailureId() {
        return failureId;
    }

    public void setFailureId(String failureId) {
        this.failureId = failureId;
    }

    public String getScreenshotId() {
        return screenshotId;
    }

    public void setScreenshotId(String screenshotId) {
        this.screenshotId = screenshotId;
    }

    public String getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(String executionDate) {
        this.executionDate = executionDate;
    }

    public String getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
    }

}
