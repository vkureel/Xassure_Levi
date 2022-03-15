package com.xassure.framework.webDriverFactory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.util.Map;
import java.util.logging.Level;

public class Safari_Driver implements IDriver {
    private WebDriver driver;

    public static void main(String[] args) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setCapability("handlesAlerts", true);
        desiredCapabilities.setCapability("cssSelectorsEnabled", true);
//        desiredCapabilities.setCapability("automaticInspection", true);
        desiredCapabilities.setCapability("automaticProfiling", true);
        desiredCapabilities.setCapability("nativeEvents", true);

        LoggingPreferences loggingPreferences = new LoggingPreferences();
        loggingPreferences.enable(LogType.BROWSER, Level.ALL);
        loggingPreferences.enable(LogType.PERFORMANCE, Level.ALL);
        desiredCapabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);

        SafariOptions safariOptions = new SafariOptions(desiredCapabilities);
        safariOptions.merge(desiredCapabilities);

        WebDriver driver = new SafariDriver(safariOptions);
        driver.get("https://reg-002.levi-site.com/US/en_US/?nextgen=true");
    }

    @Override
    public WebDriver getDriver(String... params) {


        String headLessExecution = "false";
        Map<String, String> mobileEmulation;
        String deviceName;
        if (params.length == 1) {
            headLessExecution = params[0];
        }
        if (params.length == 2) {
            throw new RuntimeException("mobile Emulation is not enabled for safari");
        }


        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setJavascriptEnabled(true);
//        desiredCapabilities.setCapability("handlesAlerts", true);
//        desiredCapabilities.setCapability("cssSelectorsEnabled", true);
////        desiredCapabilities.setCapability("automaticInspection", true);
////        desiredCapabilities.setCapability("automaticProfiling", true);
//        desiredCapabilities.setCapability("Allow Remote Automation", true);
//        desiredCapabilities.setCapability("javascriptEnabled", true);
//        desiredCapabilities.setCapability("nativeEvents", true);
        desiredCapabilities.setJavascriptEnabled(true);
        desiredCapabilities.setCapability("handlesAlerts", true);
        desiredCapabilities.setCapability("cssSelectorsEnabled", true);
//        desiredCapabilities.setCapability("automaticInspection", true);
        desiredCapabilities.setCapability("automaticProfiling", true);
        desiredCapabilities.setCapability("nativeEvents", true);

        LoggingPreferences loggingPreferences = new LoggingPreferences();
        loggingPreferences.enable(LogType.BROWSER, Level.ALL);
//        loggingPreferences.enable(LogType.PERFORMANCE, Level.ALL);
        desiredCapabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);

        SafariOptions safariOptions = new SafariOptions(desiredCapabilities);
        safariOptions.merge(desiredCapabilities);
//        safariOptions.setAutomaticProfiling(true);
//        safariOptions.setAutomaticProfiling(true);

        driver = new SafariDriver(safariOptions);
        driver.manage().window().maximize();
        return driver;
    }
}
