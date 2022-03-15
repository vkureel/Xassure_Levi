package com.xassure.framework.driver;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.xassure.enums.Drivers;
import com.xassure.framework.webDriverFactory.BrowserStackDriver;
import com.xassure.framework.webDriverFactory.DriverBinding;
import com.xassure.reporting.testCaseDetails.TestCaseData;
import com.xassure.reporting.utilities.PropertiesFileHandler;
import com.xassure.utilities.MobileDesiredCapabilities;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ControlsMobileProvider implements Provider<Controls> {

    public static volatile Map<Long, Controls> mobileDriverMap;
    //    public static volatile Map<Long, WebLibrary> mobileWebDriverMap;
    public static volatile List<TestCaseData> testCaseDataList;
    public static volatile Map<Long, AppiumDriverLocalService> appiumDriverLocalServiceMap;
    public static volatile Map<Long, URL> urlMap;
    private static AppiumDriverLocalService service = null;
    private static String APP_BUNDLE_ID;
    private URL url;
    private TestCaseData testCaseData;
    private MobileDesiredCapabilities mobileDesiredCapabilities;
    private AppiumServiceBuilder builder = null;
    private int retriesToStartAppium = 0;

    @Inject
    public ControlsMobileProvider(TestCaseData testCaseData) {
        this.testCaseData = testCaseData;
    }

    /**
     * Stop Appium:-
     */
    public static void stopAppium() {
//        Long id = Thread.currentThread().getId();

//        service = appiumDriverLocalServiceMap.get(id);
//        if (service == null || service.isRunning()) {

//            if (service != null)
//                service.stop();

        try {
            Runtime rt = Runtime.getRuntime();

//            String shellScropt = "kill -9 2253";
//            String[] args = {"osascript","-e",shellScropt};
//            Process proc = rt.exec("ping http://0.0.0.0:4723");
            String[] args = new String[]{"kill", "-9", "$(sudo lsof -i tcp:4723)"};
            Process proc = new ProcessBuilder(args).start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
        }
//        }
    }

    public static void main(String[] args) {

//        TestCaseData testCaseData = new TestCaseData();
//        testCaseData.setDeviceType("iOS");
//        testCaseData.setMobileConfig("R_APP_iPhoneXs");
//        testCaseData.setPlatform("mobile");

        TestCaseData testCaseData = new TestCaseData();
        testCaseData.setDeviceType("Android");
        testCaseData.setConfig("S_Nexus6");
        testCaseData.setBrowser("Chrome");
        testCaseData.setPlatform("mobile");


        ControlsMobileProvider controlsMobileProvider = new ControlsMobileProvider(testCaseData);
//         Start Appium Driver:-
        Controls controls = controlsMobileProvider.get();

//         Stop Appium
        controlsMobileProvider.stopAppium();


    }

    /**
     * Quit Appium Driver:-
     */
    public synchronized static void quitAppiumDriver() {

        Long id = Thread.currentThread().getId();
        mobileDriverMap.remove(id);

    }

    @Override
    public Controls get() {
        long threadId = Thread.currentThread().getId();

        if (testCaseDataList == null) {
            testCaseDataList = new ArrayList<>();
            appiumDriverLocalServiceMap = new HashMap<>();
            urlMap = new HashMap<>();
            mobileDriverMap = new HashMap<>();
        }

        if (mobileDriverMap.get(threadId) == null) {
            if (!testCaseDataList.contains(testCaseData) ||
                    !(mobileDriverMap.containsKey(threadId))) {
                if ((
                        testCaseData.getBrowser().equalsIgnoreCase(Drivers.BROWSERSTACK.driver) ||
                                testCaseData.getBrowser().equalsIgnoreCase(Drivers.EXPERITEST.driver) ||
                                testCaseData.getBrowser().equalsIgnoreCase(Drivers.CHROME.driver) ||
                                testCaseData.getDeviceType() == null)) {

                    if (testCaseData.getBrowser().equalsIgnoreCase(Drivers.BROWSERSTACK.driver) &&
                            testCaseData.getPlatform().equalsIgnoreCase("mobile")) {
                        mobileDriverMap.put(threadId, createBrowserStackMobileDriver());

                    } else if (testCaseData.getBrowser().equalsIgnoreCase(Drivers.EXPERITEST.driver) &&
                            testCaseData.getPlatform().equalsIgnoreCase("mobile")) {
                        mobileDriverMap.put(threadId, createExperitestMobileDriver());

                    } else {
                        mobileDriverMap.put(threadId, createWebDriverInstance());

                    }
                } else {
                    mobileDriverMap.put(threadId, createMobileLibraryUsingAppiumDriver());
                }
            }
        }
        return mobileDriverMap.get(threadId);
    }

    private synchronized Controls createBrowserStackMobileDriver() {
        String configName = testCaseData.getConfig();
        String testCaseName = testCaseData.getTestCaseName();
        return new MobileControlsLibrary(new BrowserStackDriver().getAppiumDriver(configName, testCaseName));
    }

    private synchronized Controls createExperitestMobileDriver() {
//        String configName = testCaseData.getConfig();
//        String testCaseName = testCaseData.getTestCaseName();
//        AppiumDriver<MobileElement> driver = new ExperitestDriver().getAppiumDriver(configName, testCaseName);
//        SeeTestClient seeTestClient = new SeeTestClient(driver);
//        seeTestClient.startCaptureNetworkDump("/Users/levi/Documents/Github/levi-app-automation/networklogs/device.pcap");
//        MobileControlsLibrary mobileControlsLibrary = new MobileControlsLibrary(driver);
//        mobileControlsLibrary.setSeeTestClient(seeTestClient);
//        return mobileControlsLibrary;
        return null;

    }

    /**
     * Create Browser Stack Driver
     */
    synchronized Controls createWebDriverInstance() {
        Injector injectorDriver = Guice.createInjector(new DriverBinding());
        Controls webLibrary = injectorDriver.getInstance(WebLibrary.class);
        String browser = testCaseData.getBrowser();
        String headless = new PropertiesFileHandler().readProperty("setupConfig", "headLessExecution");
        String configName = testCaseData.getConfig();
        String testCaseName = testCaseData.getTestCaseName();
        webLibrary.setDriver(browser, headless, configName, testCaseName);
        return webLibrary;
    }

    synchronized Controls createMobileLibraryUsingAppiumDriver() {

        long threadId = Thread.currentThread().getId();

        mobileDesiredCapabilities = new MobileDesiredCapabilities(testCaseData);
        Map<String, Object> appiumConfiguration = mobileDesiredCapabilities.readAppiumConfig();

        // Start Appium if not started:-
        if (appiumDriverLocalServiceMap.containsKey(threadId)) {
            service = appiumDriverLocalServiceMap.get(threadId);
            url = urlMap.get(threadId);
        } else {

            APP_BUNDLE_ID = (String) appiumConfiguration.get("bundleId");
            startAppium(appiumConfiguration);
        }

        return createDriver(mobileDesiredCapabilities.readMobileConfig());
    }

    /**
     * Star the appium server with appium capabilities:-
     *
     * @param appiumConfiguration
     */
    void startAppium(Map<String, Object> appiumConfiguration) {

        if (service == null ||
                (!service.isRunning())) {


//            Desired Capabilities:-
            DesiredCapabilities appiumCapabilities;
            appiumCapabilities = new DesiredCapabilities();
            try {

                appiumCapabilities.setCapability("noReset",
                        (String) appiumConfiguration.get("noReset"));

                appiumCapabilities.setCapability("newCommandTimeout",
                        appiumConfiguration.get("appiumCmdTimeOut"));

                HashMap<String, String> environment = new HashMap<String, String>();

                // Build the Appium service
                builder = new AppiumServiceBuilder()
                        .usingDriverExecutable(new File((String) appiumConfiguration.get("nodepath")))
                        .withAppiumJS(new File((String) appiumConfiguration.get("appiumJsPath")))
                        .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                        .withArgument(GeneralServerFlag.LOG_LEVEL, "error")
                        .usingPort(4723)
                        .withEnvironment(environment);

//            In Case of android set android home
                if (testCaseData.getDeviceType().equalsIgnoreCase("android"))
                    builder.withEnvironment(ImmutableMap.of("ANDROID_HOME", (String) appiumConfiguration.get("androidHome")));

//                // Start the server with the builder
                service = AppiumDriverLocalService.buildService(builder);
                service.start();
                url = service.getUrl();
                System.out.println("Appium has been started successfully on this URI: " + url);
                Long threadId = Thread.currentThread().getId();

                urlMap.put(threadId, url);
                appiumDriverLocalServiceMap.put(threadId, service);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to start the appium, please resolve");
            }
        }
    }
   
    Controls createDriver(DesiredCapabilities capabilities) {
        AppiumDriver<MobileElement> appiumDriver = null;
        if (testCaseData.getDeviceType().equalsIgnoreCase("ios")) {

            appiumDriver = new IOSDriver(url, capabilities);
        } else if (testCaseData.getDeviceType().equalsIgnoreCase("android")) {
//            ChromeOptions chromeOptions = new ChromeOptions();
//            chromeOptions.setExperimentalOption("w3c", false);
//            capabilities.merge(chromeOptions);
            appiumDriver = new AndroidDriver<MobileElement>(url, capabilities);
        }
        appiumDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);


        return new MobileControlsLibrary(appiumDriver);

    }
//    public static void removeApp() {
//        Long id = Thread.currentThread().getId();
//        mobileDriverMap.get(id).getDriver().removeApp(APP_BUNDLE_ID);
//    }


}
