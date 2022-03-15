package com.xassure.framework.webDriverFactory;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.xassure.enums.App;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import net.minidev.json.JSONArray;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

public class ExperitestDriver implements IDriver {

    public RemoteWebDriver getDriver(String... params) {
        String configName = "";
        String testCasename = "";
        if (params.length == 2) {
            configName = params[0];
            testCasename = params[1];
        }
        if (params.length == 3) {
            throw new RuntimeException("mobile Emulation is not browser stack");
        }

        DocumentContext documentContext = null;
        try {
            documentContext = JsonPath.parse(new File(App.EXPERITESTCONFIGURATION.property));
        } catch (IOException e) {
            throw new RuntimeException("Please specify the correct browser stack configuration path");
        }


        //final String URL = "https://uscloud.experitest.com/wd/hub";
        final String URL ="https://cloud.seetest.io/wd/hub";
        RemoteWebDriver driver;
        try {
            driver = new RemoteWebDriver(new URL(URL), setDesiredCapabilities(documentContext, configName, testCasename));


            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL exception , please verify your username" +
                    " and automation key: " + URL);
        }

    }


    /**
     * Set desired capabilities for browser stack:-
     *
     * @param documentContext
     * @param configName
     * @return
     */
    private DesiredCapabilities setDesiredCapabilities(DocumentContext documentContext, String configName, String testcaseName) {
        try {
            final String accessKey = documentContext.read("$.access_key");

            JSONArray jsonArray = documentContext.read("$..configuration[?(@.configName=='" + configName + "')]");
            LinkedHashMap<String, String> capabilities = (LinkedHashMap<String, String>) jsonArray.get(0);
            DesiredCapabilities caps = new DesiredCapabilities();
            System.out.println("TESTCASE NAME: " + testcaseName);
            caps.setCapability("accessKey", accessKey);
            for (String capability : capabilities.keySet()) {
                if (capability.equalsIgnoreCase("name")) {
                    caps.setCapability("name", testcaseName);
                } else {
                    if(!capability.equalsIgnoreCase("configName")){
                        caps.setCapability(capability, capabilities.get(capability));
                    }
                }
            }
            return caps;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AppiumDriver getAppiumDriver(String... params) {
        String configName = "";
        String testCaseName = "";
        if (params.length == 2) {
            configName = params[0];
            testCaseName = params[1];
        }
        if (params.length == 3) {
            throw new RuntimeException("mobile Emulation is not browser stack");
        }

        DocumentContext documentContext = null;
        try {
            documentContext = JsonPath.parse(new File(App.EXPERITESTCONFIGURATION.property));
        } catch (IOException e) {
            throw new RuntimeException("Please specify the correct browser stack configuration path");
        }
        //final String URL = "https://uscloud.experitest.com/wd/hub";
        final String URL ="https://cloud.seetest.io/wd/hub";

        AppiumDriver driver;
        try {
            if(configName.contains("IOS")){
                driver = new IOSDriver(new URL(URL), setDesiredCapabilities(documentContext, configName, testCaseName));

            }else{
                driver = new AndroidDriver(new URL(URL), setDesiredCapabilities(documentContext, configName, testCaseName));

            }
            driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL exception , please verify your username" +
                    " and automation key: " + URL);
        }

    }

}
