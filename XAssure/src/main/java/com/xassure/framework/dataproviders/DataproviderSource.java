package com.xassure.framework.dataproviders;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.xassure.enums.App;
import com.xassure.enums.Drivers;
import net.minidev.json.JSONArray;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class DataproviderSource {
    static DocumentContext documentContext;

    @DataProvider(name = "BrowserDataProvider", parallel = true)
    public static Object[][] getBrowserTestData(ITestContext context) {
        String testName = context.getAllTestMethods()[0].getMethodName();
        return getTestConfigData(testName);
    }

    public static Object[][] getTestConfigData(String testCaseName) {
        System.out.println("TEST NAME IN DATAPROVIDER: " + testCaseName);
        try {
            ArrayList<ArrayList<Object>> testConfiguration = new ArrayList();
            String fileName = System.getProperty("testConfig");
            String filePath = App.LOCALE_DATAPROVIDER.property + File.separator + fileName + ".json";
            if (documentContext == null)
                documentContext = JsonPath.parse(
                        new File(filePath));


            JSONArray jsonArray = documentContext.read("[?(@.tc_Name=='" + testCaseName + "')].testConfig");
            JSONArray configInfo = (JSONArray) jsonArray.get(0);
            for (Object config : configInfo) {
                LinkedHashMap<String, String> configDetails = (LinkedHashMap) config;


                TestCaseConfig caseConfig = new TestCaseConfig(testCaseName, configDetails.get("browser"))
                        .setCountry(configDetails.get("country"))
                        .setLocale(configDetails.get("locale"));

                if (configDetails.containsKey("deviceType")) {

                    setupMobileConfiguration(caseConfig, configDetails);
                } else {
                    setupBrowserAndCloudConfigurations(caseConfig, configDetails);
                }
//                if (configDetails.get("browser").equalsIgnoreCase(Drivers.BROWSERSTACK.driver)) {
//
//                    caseConfig.setConfig(configDetails.get("configName"));
//                }
//
//                if (configDetails.containsKey("platform")) {
//                    caseConfig.setPlatform(configDetails.get("platform"));
//                    String platform = configDetails.get("platform");
//                    if (platform.equalsIgnoreCase("mobile")) {
//
////                        Setup for mobile emulators on chrome configuration:-
//                        if (configDetails.containsKey("browser")) {
//                            if (!configDetails.containsKey("configName")) {
//                                throw new RuntimeException("Please specify configuration for browser stack");
//                            }
//                            caseConfig.setConfig(configDetails.get("configName"));
//                        } else {
//                            if (configDetails.containsKey("mobileConfig")) {
//                                caseConfig.setConfig(configDetails.get("mobileConfig"));
//                                caseConfig.setDeviceType(configDetails.get("deviceType"));
//                            }
//                        }
//
//
//                    }
//                }

                System.out.println(caseConfig.toString());
                testConfiguration.add(new ArrayList<>(Arrays.asList(caseConfig)));
            }

            return testConfiguration.stream().map(u -> u.toArray(new Object[0])).toArray(Object[][]::new);
        } catch (IOException e) {
            throw new RuntimeException("Please provide the correct test data for dataprovider-");
        }

    }

    public static void main(String[] args) {
        String testCaseName = "validateLeviLogo";
        Object[] objects = getTestConfigData(testCaseName);
        System.out.println(objects);

        ArrayList<ArrayList<String>> mainList = new ArrayList<ArrayList<String>>();
        mainList.add(new ArrayList(Arrays.asList("String")));
        mainList.add(new ArrayList(Arrays.asList("String2")));
        mainList.add(new ArrayList(Arrays.asList("String3")));
        String[][] stringArray = mainList.stream().map(u -> u.toArray(new String[0])).toArray(String[][]::new);
        System.out.println(stringArray);
//        Object arr = new Object[][]{
//                {"Chrome", "CA"},
//                {"Chrome", "US"},
//                {"Chrome", "FR"}};
//        System.out.println(arr);
    }

    private static void setupBrowserAndCloudConfigurations(TestCaseConfig caseConfig, LinkedHashMap<String, String> configDetails) {
        if (configDetails.get("browser").
                equalsIgnoreCase(Drivers.BROWSERSTACK.driver) || configDetails.get("browser").
                equalsIgnoreCase(Drivers.EXPERITEST.driver)
        ) {

            caseConfig.setConfig(configDetails.get("configName"));
        }

        if (configDetails.containsKey("platform")) {
            caseConfig.setPlatform(configDetails.get("platform"));
            String platform = configDetails.get("platform");
            if (platform.equalsIgnoreCase("mobile")) {

//                        Setup for mobile emulators on chrome configuration:-
                if (configDetails.containsKey("browser")) {
                    if (!configDetails.containsKey("configName")) {
                        throw new RuntimeException("Please specify configuration for browser stack");
                    }
                    caseConfig.setConfig(configDetails.get("configName"));
                } else {
                    if (configDetails.containsKey("mobileConfig")) {
                        caseConfig.setConfig(configDetails.get("mobileConfig"));
                        caseConfig.setDeviceType(configDetails.get("deviceType"));
                    }
                }


            }
        }

    }

    private static void setupMobileConfiguration(TestCaseConfig caseConfig, LinkedHashMap<String, String> configDetails) {
        if (configDetails.containsKey("deviceType")) {
            caseConfig.setPlatform(configDetails.get("platform"));
            caseConfig.setDeviceType(configDetails.get("deviceType"));
            caseConfig.setConfig(configDetails.get("mobileConfig"));
            caseConfig.setCountry(configDetails.get("country"));
            caseConfig.setLocale(configDetails.get("locale"));
            caseConfig.setPlatform(configDetails.get("platform"));

        }

    }
}