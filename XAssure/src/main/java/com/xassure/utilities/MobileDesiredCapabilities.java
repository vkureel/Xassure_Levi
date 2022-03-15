package com.xassure.utilities;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.xassure.reporting.testCaseDetails.TestCaseData;
import net.minidev.json.JSONArray;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MobileDesiredCapabilities {

    private TestCaseData testCaseData;

    public MobileDesiredCapabilities(TestCaseData testCaseData) {
        this.testCaseData = testCaseData;
    }

    public static void main(String[] args) {
//        Test Data for Read Device BROWSer - IOS

        TestCaseData testCaseData = new TestCaseData();
        testCaseData.setDeviceType("iOS");
        testCaseData.setConfig("R_iPhoneXs");
        testCaseData.setBrowser("Chrome");

        //        Test Data for Real Device APP - IOS

//        TestCaseData testCaseData = new TestCaseData();
//        testCaseData.setDeviceType("iOS");
//        testCaseData.setPlatform("mobiles");
//        testCaseData.setMobileConfig("R_APP_iPhoneXs");

        MobileDesiredCapabilities mobileDesiredCapabilities = new MobileDesiredCapabilities(testCaseData);
        mobileDesiredCapabilities.readMobileConfig();
    }

    /**
     * Read Mobile Config and get desired capabilities
     *
     * @return
     */
    public DesiredCapabilities readMobileConfig() {


        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        try {
            String platformType = testCaseData.getDeviceType();
            File capabilityFilePath = null;
            if (platformType.equalsIgnoreCase("ios")) {
                capabilityFilePath = new File("./src/main/resources/setup/mobileConfig/iosConfig.json");
            } else if (platformType.equalsIgnoreCase("android")) {
                capabilityFilePath = new File("./src/main/resources/setup/mobileConfig/androidConfig.json");
            }
            DocumentContext deviceCapabilityDetails = JsonPath.parse(capabilityFilePath);
            JSONArray deviceCapDetails = deviceCapabilityDetails.read("$..configurations[?(@.configName=='" + testCaseData.getConfig() + "')]");
            LinkedHashMap deviceConfig = (LinkedHashMap) deviceCapDetails.get(0);

            LinkedHashMap<String, Object> deviceDetails = (LinkedHashMap) deviceConfig.get("capabilities");
            for (String deviceCap : deviceDetails.keySet()) {
                System.out.println("Capability name: " + deviceCap + " : " + deviceDetails.get(deviceCap));

                if (deviceCap.equalsIgnoreCase("browserName")) {
                    String browserName = testCaseData.getBrowser();
                    if (browserName == null || browserName.isEmpty()) {
                        desiredCapabilities.setCapability(deviceCap, deviceDetails.get(deviceCap));
                    } else {

                        desiredCapabilities.setCapability(deviceCap, browserName);

                    }
                } else if (deviceCap.equalsIgnoreCase("app")) {

                    String appFilePath = (String) deviceDetails.get("app");
                    if (appFilePath.isEmpty()) {
                        desiredCapabilities.setCapability(deviceCap, deviceDetails.get(deviceCap));
                    } else {

                        File file = new File(appFilePath);
                        if (!file.exists()) {
                            throw new RuntimeException("Please specify the correct APP path");
                        }
                        desiredCapabilities.setCapability(deviceCap, file.getAbsolutePath());
                    }
                } else if (deviceCap.equalsIgnoreCase("appPackage")) {
                    desiredCapabilities.setCapability("appPackage", deviceDetails.get(deviceCap));

                } else {
                    desiredCapabilities.setCapability(deviceCap, deviceDetails.get(deviceCap));
                }

            }

            String executionType = (String) deviceConfig.get("execution_type");
            if (executionType.equalsIgnoreCase("realDevice")) {
                JSONArray jsonArray = deviceCapabilityDetails.read("$..baseConfig[?(@.execution_type=='realDevice')].configurations");
                LinkedHashMap<String, Object> baseCapabilities = (LinkedHashMap) jsonArray.get(0);
                for (String deviceCap : baseCapabilities.keySet()) {
                    System.out.println("Capability name: " + deviceCap + " : " + baseCapabilities.get(deviceCap));
                    Object capabilityValue = baseCapabilities.get(deviceCap);
                    desiredCapabilities.setCapability(deviceCap, capabilityValue);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Please provide the correct mobile configuration details");
        }


        setupDriverExecutablePath();
        return desiredCapabilities;
    }

    /**
     * Read appium configuation details:-
     *
     * @return
     */
    public Map<String, Object> readAppiumConfig() {

        File file = new File(
                "./src/main/resources/setup/mobileConfig/mobile_configuration.json");

        DocumentContext documentContext = null;
        try {
            documentContext = JsonPath.parse(file);
        } catch (IOException e) {
            throw new RuntimeException("Please specify the correct mobile configuration details, please find exception details as" + e);
        }
        LinkedHashMap<String, Object> appiumConfiguration = documentContext.read("$.appium_Capabilities");

        return appiumConfiguration;

    }

    /**
     * Setup pass for browser configuration:-
     */
    private void setupDriverExecutablePath() {

        if (testCaseData.getBrowser() != null) {
            if (testCaseData.getBrowser().equalsIgnoreCase("chrome")) {
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver.exe");
                } else if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
                    System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver");
                }

            } else if (testCaseData.getBrowser().equalsIgnoreCase("firefox")) {
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    System.setProperty("webDriver.gecko.driver", "src/main/resources/drivers/gecko.exe");
                } else if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
                    System.setProperty("webDriver.gecko.driver", "src/main/resources/drivers/gecko");
                }
            }
        }

    }

}
