package com.xassure.framework.dataproviders;

import java.util.Objects;

public class TestCaseConfig {

    private String testCaseName;
    private String locale;
    private String browser;
    private String country;
    private String platform;
    private String device;
    private String deviceType;
    private String config;

    public TestCaseConfig(String testCaseName, String browser) {
        this.testCaseName = testCaseName;
        this.browser = browser;
    }

    public String getDevice() {
        return device;
    }

    public TestCaseConfig setDevice(String device) {
        this.device = device;
        return this;
    }

    public String getPlatform() {

        if (platform == null) {
            platform = "web";
        }
        return platform;
    }

    public TestCaseConfig setPlatform(String platform) {
        this.platform = platform;
        return this;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public TestCaseConfig setDeviceType(String deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public String getConfig() {
        return config;
    }

    public TestCaseConfig setConfig(String config) {
        this.config = config;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public TestCaseConfig setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public String getLocale() {
        return locale;
    }

    public TestCaseConfig setLocale(String locale) {
        this.locale = locale;
        return this;
    }

    public String getBrowser() {
        return browser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestCaseConfig)) return false;
        TestCaseConfig that = (TestCaseConfig) o;
        return getTestCaseName().equals(that.getTestCaseName()) &&
                Objects.equals(getLocale(), that.getLocale()) &&
                Objects.equals(getBrowser(), that.getBrowser()) &&
                Objects.equals(getCountry(), that.getCountry());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTestCaseName(), getLocale(), getBrowser(), getCountry());
    }

    @Override
    public String toString() {
        return "TestCaseConfig{" +
                "testCaseName='" + testCaseName + '\'' +
                ", locale='" + locale + '\'' +
                ", browser='" + browser + '\'' +
                ", country='" + country + '\'' +
                ", platform='" + platform + '\'' +
                ", device='" + device + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", config='" + config + '\'' +
                '}';
    }
}
