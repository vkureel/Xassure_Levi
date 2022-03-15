package com.xassure.reporting.testcasesummary;

import java.util.Objects;

public class LocaleLanguage {
    private String localeLanguage;
    private String browser;
    private int totalTestCases;
    private int pass;
    private int fail;
    private int skipped;
    private double passPercent;

    public LocaleLanguage(String localeLanguage, String browser) {
        this.localeLanguage = localeLanguage;
        this.browser = browser;
    }

    @Override
    public String toString() {
        return "LocaleLanguage{" +
                "localeLanguage='" + localeLanguage + '\'' +
                ", browser='" + browser + '\'' +
                ", totalTestCases=" + totalTestCases +
                ", pass=" + pass +
                ", fail=" + fail +
                ", skipped=" + skipped +
                ", passPercent=" + passPercent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocaleLanguage)) return false;
        LocaleLanguage that = (LocaleLanguage) o;
        return getLocaleLanguage().equals(that.getLocaleLanguage()) &&
                getBrowser().equals(that.getBrowser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocaleLanguage(), getBrowser());
    }

    public String getLocaleLanguage() {
        return localeLanguage;
    }

    public void setLocaleLanguage(String localeLanguage) {
        this.localeLanguage = localeLanguage;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public int getTotalTestCases() {
        return totalTestCases;
    }

    public void setTotalTestCases(int totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }

    public int getFail() {
        return fail;
    }

    public void setFail(int fail) {
        this.fail = fail;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public double getPassPercent() {
        return passPercent;
    }

    public void setPassPercent(double passPercent) {
        this.passPercent = passPercent;
    }
}
