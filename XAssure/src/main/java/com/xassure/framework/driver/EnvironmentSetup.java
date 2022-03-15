package com.xassure.framework.driver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.xassure.framework.dataproviders.TestCaseConfig;
import com.xassure.framework.webDriverFactory.TestCasesBinding;
import com.xassure.reporting.csvHandlers.CsvWriter;
import com.xassure.reporting.logger.LogStatus;
import com.xassure.reporting.logger.Logger;
import com.xassure.reporting.logger.Reporting;
import com.xassure.reporting.testCaseDetails.TestCaseData;
import com.xassure.reporting.utilities.EmailReport;
import com.xassure.reporting.utilities.PropertiesFileHandler;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class EnvironmentSetup {

    public static boolean quitBrowser = Boolean
            .parseBoolean(new PropertiesFileHandler().readProperty("setupConfig", "quitBowser"));
    static int testCaseCount = 0;
    static Reporting reporter = new Reporting();
    private static boolean captureScreenShotAtEveryStep = Boolean
            .parseBoolean(new PropertiesFileHandler().readProperty("setupConfig", "screenshotAtEveryStep"));
    private static boolean dbControl = Boolean
            .parseBoolean(new PropertiesFileHandler().readProperty("setupConfig", "dbControl"));
    private static boolean apiControl = Boolean
            .parseBoolean(new PropertiesFileHandler().readProperty("setupConfig", "apiControl"));
    private static boolean webAppActive = Boolean
            .parseBoolean(new PropertiesFileHandler().readProperty("webApp", "isActive"));
    public ThreadLocal<Injector> injector = new ThreadLocal<>();
    public ThreadLocal<TestCaseData> testDataInfo = new ThreadLocal<>();
    PropertiesFileHandler propFileHandle = new PropertiesFileHandler();
    Logger log = null;
    volatile Map<Long, ThreadLocal<Injector>> injectorMap = new HashMap<>();
    private String testSuitId;

    @Parameters({"releaseId", "sprintId", "testSuiteId"})
    @BeforeSuite
    public void startReporter(ITestContext context, String releaseId, String sprintId, String testSuiteId) {
        this.testSuitId = testSuiteId;
        reporter.createFolder(context.getSuite().getName(),
                captureScreenShotAtEveryStep,
                releaseId,
                sprintId,
                testSuiteId);
        if (webAppActive)
            hitRequest("/api/testSuites/testSuite/execution/" + Reporting.getRunId() + "/" + testSuiteId,
                    "POST");

    }

    public void hitRequest(String urlPath, String requestType) {
        try {
            String serverIp = propFileHandle.readProperty("webApp", "serverIp");
            String serverPort = propFileHandle.readProperty("webApp", "serverPort");
            System.out.println("server port - " + serverPort);
            URL url = new URL("http://" + serverIp + ":" + serverPort + urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(requestType);

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                //System.out.println(output);
            }

            conn.disconnect();

        } catch (UnknownHostException e) {
            System.out.println("Host not found for XAssure Web Application");
        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    @Parameters({
            "platform",
            "browser",
            "locale",
            "language",
            "deviceType",
            "mobileConfig"
    })
    @BeforeMethod
    public void beforeMethod(
            String platform,
            @Optional String browser,
            @Optional String locale,
            @Optional String country,

            @Optional String deviceType,
            @Optional String mobileConfig,
            Method method,
            @Optional Object[] dataprovidersArgs
    ) {
        TestCaseConfig testCaseConfig;

        if (dataprovidersArgs.length > 0) {
            testCaseConfig = (TestCaseConfig) dataprovidersArgs[0];
            browser = testCaseConfig.getBrowser();
            locale = testCaseConfig.getLocale();
            country = testCaseConfig.getCountry();


            if (testCaseConfig.getPlatform() != null) {

                platform = testCaseConfig.getPlatform();
            }

//            Mobile Emulator
            if (testCaseConfig.getDevice() != null) {

                deviceType = testCaseConfig.getDevice();
            }

//            Real /Simulator
            if (testCaseConfig.getDeviceType() != null) {
                deviceType = testCaseConfig.getDeviceType();

            }

            if (testCaseConfig.getConfig() != null) {

                mobileConfig = testCaseConfig.getConfig();
            }
        }

        testCaseCount = testCaseCount + 1;
        long id = Thread.currentThread().getId();

        System.out.println("Value of threadID: " + id + ": " + country + " Locale: " + locale);

        //        Setup Test Case Data:-
        TestCaseData testCasedata = new TestCaseData();
        testCasedata.setBrowser(browser);
        testCasedata.setPlatform(platform);
        testCasedata.setTestCaseModule(method.getDeclaringClass().getSimpleName());
        testCasedata.setLocale(locale);
        testCasedata.setCountry(country);
        testCasedata.setDeviceType(deviceType);
        testCasedata.setConfig(mobileConfig);
        testCasedata.setTestCaseName(method.getName());

        Test test = method.getAnnotation(Test.class);
        System.out.println("Test name is " + test.testName());
        System.out.println("Test description is " + test.description());
        testCasedata.setDescription(test.description());
        testDataInfo.set(testCasedata);

        Injector injectorInfo = Guice.createInjector(new TestCasesBinding(testCasedata));
        injectorInfo = injectorInfo.createChildInjector(new ControlsBinding());
        injector.set(injectorInfo);
        reporter.startReporter(testCasedata);
    }

    @Parameters({"platform", "browser"})
    @AfterMethod(alwaysRun = true)
    public void afterMethod(String platform,
                            @Optional String browser, ITestResult result) {
        try {



            int status = result.getStatus();
            if (status == ITestResult.FAILURE) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                result.getThrowable().printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                System.out.println(sStackTrace);
                Reporting.getLogger().log(LogStatus.FAIL, "Test FAILED ", new Exception(sStackTrace));
            } else if (status == ITestResult.SKIP) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                result.getThrowable().printStackTrace(pw);
                String sStackTrace = sw.toString(); // stack trace as a string
                System.out.println(sStackTrace);
                Reporting.getLogger().log(LogStatus.FAIL, "Test FAILED " + new Exception(sStackTrace));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();

        }
        if (quitBrowser) {

            if (testDataInfo.get().getPlatform().equalsIgnoreCase("web")) {
                Web_CloseDriver webLibrary = injector.get().getInstance(Web_CloseDriver.class);

                try {
                    webLibrary.quitDriver();

                } catch (Exception e) {

                } finally {
                    injector.remove();
                    ControlsWebProvider.removeThread();
                }
            } else if (testDataInfo.get().getPlatform().contains("mobile")) {

                Mob_CloseDriver webLibrary = injector.get().getInstance(Mob_CloseDriver.class);
                try {
                    webLibrary.quitDriver();
                } catch (Exception e) {

                } finally {
                    injector.remove();
                    ControlsMobileProvider.quitAppiumDriver();
                }


            }
        }

    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        Reporting.setExecutionFinishTime();
        ControlsMobileProvider.stopAppium();

        CsvWriter csvWriter = new CsvWriter();
        EmailReport email = new EmailReport();
        EmailReport.totalTestCases = testCaseCount;
        email.sendEmail();
        csvWriter.updateSetupCsv(testCaseCount);
        if (webAppActive)
            hitRequest("/api/testSuites/testSuite/execution/" + Reporting.getRunId() + "/" + testSuitId,
                    "PUT");

    }
}
