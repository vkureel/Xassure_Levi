package com.xassure.reporting.testCaseDetails;

import java.util.Objects;

public class TestCaseData implements TestCase {

    private String testCaseName;
    private String testCaseModule;
    private String platform;
    private String testCaseStatus;
    private String browser;
    private String pageName;
    private String locale;
    private String deviceType;
    private String configName;
    private String country;
    private boolean isExecutionStarted = false;
    private String description;
    private String language;

    public String getLanguage() {
        if (locale == null) {
            return "";
        }
        return locale.split("_")[0];
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        if (country == null) {
            country = "";
        }
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isExecutionStarted() {
        return isExecutionStarted;
    }

    public void setExecutionStarted(boolean executionStarted) {
        isExecutionStarted = executionStarted;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getConfig() {
        return configName;
    }

    public void setConfig(String configName) {
        this.configName = configName;
    }

    public String getLocale() {

        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }


    public String getPlatform() {
        if (platform == null)
            platform = "web";
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public String getTestCaseModule() {
        return testCaseModule;
    }

    public void setTestCaseModule(String testCaseModule) {
        this.testCaseModule = testCaseModule;
    }

    public String getTestCaseStatus() {

        if (testCaseStatus == null)
            return "";
        return testCaseStatus;
    }

    public void setTestCaseStatus(String testCaseStatus) {
        this.testCaseStatus = testCaseStatus;
    }

    public String getBrowser() {
        if (this.browser == null)
            return "";
        return browser.toLowerCase();
    }

    public void setBrowser(String browser) {
        if (browser != null) {
            this.browser = browser.toLowerCase();
        }
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestCaseData)) return false;
        TestCaseData that = (TestCaseData) o;
        return isExecutionStarted() == that.isExecutionStarted() &&
                Objects.equals(getTestCaseName(), that.getTestCaseName()) &&
                Objects.equals(getTestCaseModule(), that.getTestCaseModule()) &&
                Objects.equals(getPlatform(), that.getPlatform()) &&
                Objects.equals(getTestCaseStatus(), that.getTestCaseStatus()) &&
                Objects.equals(getBrowser(), that.getBrowser()) &&
                Objects.equals(getPageName(), that.getPageName()) &&
                Objects.equals(getLocale(), that.getLocale()) &&
                Objects.equals(getDeviceType(), that.getDeviceType()) &&
                Objects.equals(getConfig(), that.getConfig()) &&
                Objects.equals(getCountry(), that.getCountry());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTestCaseName(), getTestCaseModule(), getPlatform(), getTestCaseStatus(), getBrowser(),
                getPageName(), getLocale(), getDeviceType(), getConfig(), getCountry(), isExecutionStarted());
    }

    @Override
    public String toString() {
        return "TestCaseData{" +
                "testCaseName='" + testCaseName + '\'' +
                ", testCaseModule='" + testCaseModule + '\'' +
                ", platform='" + platform + '\'' +
                ", testCaseStatus='" + testCaseStatus + '\'' +
                ", browser='" + browser + '\'' +
                ", pageName='" + pageName + '\'' +
                ", locale='" + locale + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", configName='" + configName + '\'' +
                ", country='" + country + '\'' +
                ", isExecutionStarted=" + isExecutionStarted +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
