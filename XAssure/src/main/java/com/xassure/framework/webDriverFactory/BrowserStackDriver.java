package com.xassure.framework.webDriverFactory;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.xassure.enums.App;
import com.xassure.reporting.utilities.PropertiesFileHandler;
import io.appium.java_client.AppiumDriver;
import net.minidev.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BrowserStackDriver implements IDriver {

    public static void main(String[] args) throws Exception {

        WebDriver driver = new BrowserStackDriver().getDriver("BR_STACK_CHROME_MAC");
//        driver.get("https://sprint-400.levi-site.com/US/en_US/?nextgen=true");
        driver.get("https://sprint-400.dockers-site.com/US/en_US/?nextgen=true");
        takeSnapShot(driver, "./test.png");
    }

    static void takeSnapShot(WebDriver webdriver, String fileWithPath) throws Exception {
//Convert web driver object to TakeScreenshot
        TakesScreenshot scrShot = ((TakesScreenshot) webdriver);
//Call getScreenshotAs method to create image file
        File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);
//Move image file to new destination
        File DestFile = new File(fileWithPath);
//Copy file at destination
        FileUtils.copyFile(SrcFile, DestFile);
    }

    /**
     * Get Browser stack driver:-
     *
     * @param params
     * @return
     */
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
            documentContext = JsonPath.parse(new File(App.BROWSERCONFIGURATIONPATH.property));
        } catch (IOException e) {
            throw new RuntimeException("Please specify the correct browser stack configuration path");
        }
        final String USERNAME = documentContext.read("$.br_stack_username");
        final String AUTOMATE_KEY = documentContext.read("$.br_stack__automationkey");

        final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";
        RemoteWebDriver driver;
        try {
            driver = new RemoteWebDriver(new URL(URL), setDesiredCapabilities(documentContext, configName, testCasename));
            return driver;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL exception , please verify your username" +
                    " and automation key: " + URL);
        }

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
            documentContext = JsonPath.parse(new File(App.BROWSERCONFIGURATIONPATH.property));
        } catch (IOException e) {
            throw new RuntimeException("Please specify the correct browser stack configuration path");
        }
        final String USERNAME = documentContext.read("$.br_stack_username");
        final String AUTOMATE_KEY = documentContext.read("$.br_stack__automationkey");

        final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";
        AppiumDriver driver;
        try {
            driver = new AppiumDriver<>(new URL(URL), setDesiredCapabilities(documentContext, configName, testCaseName));
            driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
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

            JSONArray jsonArray = documentContext.read("$..configuration[?(@.configName=='" + configName + "')]");
            LinkedHashMap<String, String> capabilities = (LinkedHashMap<String, String>) jsonArray.get(0);
            DesiredCapabilities caps = new DesiredCapabilities();
            System.out.println("TESTCASE NAME: " + testcaseName);
            for (String capability : capabilities.keySet()) {
                if (capability.equalsIgnoreCase("name")) {

                    caps.setCapability("name", testcaseName);
                } else {
                    if(!capability.equalsIgnoreCase("configName")){
                        caps.setCapability(capability, capabilities.get(capability));

                    }

                }
            }
//            ChromeOptions option = new ChromeOptions();
//            option.addArguments("--no-sandbox");
//            option.addArguments("--disable-gpu");
//        option.addArguments("--disable-dev-shm-usage");
//        option.addArguments("--disable-infobars");
//        option.addArguments("--disable-default-apps");
//        option.addArguments("--allow-insecure-localhost");
//            caps.setCapability(ChromeOptions.CAPABILITY, option);

            JSONArray localExecutionDetails = documentContext.read("$..localExecution");
            String localExection = (String) localExecutionDetails.get(0);

            if (localExection.equalsIgnoreCase("true")) {
                caps.setCapability("browserstack.local", "true");
                caps.setCapability("forcelocal", "true");
            }


            return caps;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
