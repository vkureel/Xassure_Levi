package com.xassure.reporting.logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xassure.reporting.beans.DetailSteps;
import com.xassure.reporting.beans.Locators;
import com.xassure.reporting.csvHandlers.CsvWriter;
import com.xassure.reporting.testCaseDetails.TestCaseData;
import com.xassure.reporting.utilities.ErrorJson;
import com.xassure.reporting.utilities.ScreenshotHandler;

import java.util.Map;
import java.util.Random;

public class Logger {

    //        public static Map<String, TestCaseData> testcaseDataMap = new HashMap<>();
    private volatile TestCaseData testCaseData;
    private int failCount = 0;

    public Logger() {
    }

    public Logger(TestCaseData testCaseData) {
        this.testCaseData = testCaseData;
    }

    public TestCaseData getTestCaseData() {
        return testCaseData;
    }

    public void log(LogStatus status, String stepDescription) {
        DetailSteps detailStep = new DetailSteps();

//        Long threadId = Thread.currentThread().getId();
//        TestCaseData testCaseData = Reporting.getThreadTCMap().get(threadId);
        TestCaseData testCaseData = Reporting.getThreadTCMap();

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement element = stackTrace[2];
        String className = element.getClassName();
        String pageObject = "";
        if (className.contains("xassure")) {

            element = stackTrace[3];
            className = element.getClassName();
            className = className.substring(className.lastIndexOf("->") + 1);
            pageObject = className + "." + element.getMethodName();
        } else {

            className = element.getClassName();
            className = className.substring(className.lastIndexOf("->") + 1);
            pageObject = className + "." + element.getMethodName();

        }
//        String testCaseName = testCaseData.getTestCaseName();
//        if (testcaseDataMap.containsKey(testCaseName)) {
//        if (testCaseData.getTestCaseStatus() != null &&
//                (!getTestCaseData().getTestCaseStatus().equalsIgnoreCase("fail"))) {
//            String testCaseStatus;
//            if (status.equals(LogStatus.FAIL) || status.equals(LogStatus.WARNING)) {
//                testCaseStatus = status.name();
//            } else {
//                testCaseStatus = "PASS";
//            }
//
//            getTestCaseData().setTestCaseStatus(testCaseStatus);
//            getTestCaseData().setPageName(pageObject);
//        } else {
        String testCaseStatus;
        if (!testCaseData.getTestCaseStatus().equalsIgnoreCase("fail")) {

            if (status.equals(LogStatus.FAIL) || status.equals(LogStatus.WARNING)) {
                testCaseStatus = status.name();

            } else {
                testCaseStatus = "PASS";
            }
            testCaseData.setTestCaseStatus(testCaseStatus);
        }
        testCaseData.setPageName(pageObject);
//        }
//        }
//        else {
//            testCaseData.setTestCaseStatus(status.name());
//            testCaseData.setPageName(pageObject);
//            testcaseDataMap.put(testCaseName, testCaseData);
//        }

        detailStep.setExecutionDate(Reporting.getCurrentDate());
        detailStep.setExecutionTime(Reporting.getCurrentTime());
        detailStep.setRunId(Reporting.getRunId());
        detailStep.setTestCaseName(testCaseData.getTestCaseName());
        detailStep.setModule(testCaseData.getTestCaseModule());
        detailStep.setPageName(pageObject);
        detailStep.setTestStep(stepDescription.replaceAll(",", ":"));
        detailStep.setTestStepStatus(status.name());
        detailStep.setBrowserName(testCaseData.getBrowser());


        detailStep.setDescription(testCaseData.getDescription());
        detailStep.setLocale(testCaseData.getCountry());
        detailStep.setLang(testCaseData.getLanguage());
        String OS = System.getProperty("os.name").toLowerCase();
        detailStep.setOs(OS);

        if (status.equals(LogStatus.FAIL) && Reporting.getDriver() != null) {
            failCount++;
            System.out.println(">>>>>>>> FAILED TEST CASE >>>>>>>>>" + testCaseData.getTestCaseName()
                    + ">>>>> EXCEPTION METHOD>>> STATUS >>>>> " + status + " FAIL COUNT: " + failCount);
            detailStep.setScreenshotId(ScreenshotHandler.captureScreenShot(Reporting.getDriver()));
        }
        new CsvWriter().writeDetailCsv(detailStep);
        stopExecution();
    }

    public void log(LogStatus status, String stepDescription, String expected, String actual) {
        DetailSteps detailStep = new DetailSteps();

        Long threadId = Thread.currentThread().getId();
//        TestCaseData testCaseData = Reporting.getThreadTCMap().get(threadId);
        TestCaseData testCaseData = Reporting.getThreadTCMap();

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement element = stackTrace[2];
        String className = element.getClassName();
        String pageObject = "";
        if (className.contains("xassure")) {

            element = stackTrace[3];
            className = element.getClassName();
            className = className.substring(className.lastIndexOf("->") + 1);
            pageObject = className + "." + element.getMethodName();
        } else {

            className = element.getClassName();
            className = className.substring(className.lastIndexOf("->") + 1);
            pageObject = className + "." + element.getMethodName();

        }
        String testCaseName = testCaseData.getTestCaseName();
//        if (testcaseDataMap.containsKey(testCaseName)) {
//            if (!(testcaseDataMap.get(testCaseName).getTestCaseStatus().equalsIgnoreCase("fail"))) {
//                String testCaseStatus;
//                if (status.equals(LogStatus.FAIL) || status.equals(LogStatus.WARNING)) {
//                    testCaseStatus = status.name();
//                } else {
//                    testCaseStatus = "PASS";
//                }
//
//
//                testcaseDataMap.get(testCaseName).setTestCaseStatus(testCaseStatus);
//                testcaseDataMap.get(testCaseName).setPageName(pageObject);
//            }
//        } else {
//            testCaseData.setTestCaseStatus(status.name());
//            testCaseData.setPageName(pageObject);
//            testcaseDataMap.put(testCaseName, testCaseData);
//        }


        String testCaseStatus;
        if (!testCaseData.getTestCaseStatus().equalsIgnoreCase("fail")) {

            if (status.equals(LogStatus.FAIL) || status.equals(LogStatus.WARNING)) {
                testCaseStatus = status.name();

            } else {
                testCaseStatus = "PASS";
            }
            testCaseData.setTestCaseStatus(testCaseStatus);
        }

        detailStep.setExecutionDate(Reporting.getCurrentDate());
        detailStep.setExecutionTime(Reporting.getCurrentTime());
        detailStep.setRunId(Reporting.getRunId());
        detailStep.setTestCaseName(testCaseData.getTestCaseName());
        detailStep.setModule(testCaseData.getTestCaseModule());
        detailStep.setPageName(pageObject);
        detailStep.setTestStep(stepDescription + ", Expected: '" + expected + "', Actual: '" + actual + "'");
        detailStep.setTestStepStatus(status.name());
        detailStep.setBrowserName(testCaseData.getBrowser());
        detailStep.setLocale(testCaseData.getCountry());
        detailStep.setLang(testCaseData.getLanguage());
        detailStep.setDescription(testCaseData.getDescription());
        String OS = System.getProperty("os.name").toLowerCase();
        detailStep.setOs(OS);
        if (status.equals(LogStatus.FAIL) && Reporting.getDriver() != null) {
            failCount++;
            System.out.println(">>>>>>>> FAILED TEST CASE >>>>>>>>>" + testCaseData.getTestCaseName()
                    + ">>>>> EXCEPTION METHOD>>> STATUS >>>>> " + status + " FAIL COUNT: " + failCount);
            detailStep.setScreenshotId(ScreenshotHandler.captureScreenShot(Reporting.getDriver()));
        }
        new CsvWriter().writeDetailCsv(detailStep);
        stopExecution();

    }

    public void log(LogStatus status, String stepDescription, Exception e) {
        DetailSteps detailStep = new DetailSteps();

        Long threadId = Thread.currentThread().getId();
//        TestCaseData testCaseData = Reporting.getThreadTCMap().get(threadId);
        TestCaseData testCaseData = Reporting.getThreadTCMap();

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement element = stackTrace[2];
        String className = element.getClassName();
        String pageObject = "";

        if (className.contains("xassure")) {

            element = stackTrace[3];
            className = element.getClassName();
            className = className.substring(className.lastIndexOf("->") + 1);
            pageObject = className + "." + element.getMethodName();
        } else {

            className = element.getClassName();
            className = className.substring(className.lastIndexOf("->") + 1);
            pageObject = className + "." + element.getMethodName();

        }
        String testCaseName = testCaseData.getTestCaseName();
//        if (testcaseDataMap.containsKey(testCaseName)) {
//            if (!(testcaseDataMap.get(testCaseName).getTestCaseStatus().equalsIgnoreCase("fail"))) {
//                String testCaseStatus;
//                if (status.equals(LogStatus.FAIL) || status.equals(LogStatus.WARNING)) {
//                    testCaseStatus = status.name();
//                } else {
//                    testCaseStatus = "PASS";
//                }
//
//                testcaseDataMap.get(testCaseName).setTestCaseStatus(testCaseStatus);
//                testcaseDataMap.get(testCaseName).setPageName(pageObject);
//            }
//        } else {
//            testCaseData.setTestCaseStatus(status.name());
//            testCaseData.setPageName(pageObject);
//            testcaseDataMap.put(testCaseName, testCaseData);
//        }


        String testCaseStatus;
        if (!testCaseData.getTestCaseStatus().equalsIgnoreCase("fail")) {

            if (status.equals(LogStatus.FAIL) || status.equals(LogStatus.WARNING)) {
                testCaseStatus = status.name();

            } else {
                testCaseStatus = "PASS";
            }
            testCaseData.setTestCaseStatus(testCaseStatus);
        }

        String failureId = failureId();
        ErrorJson.appendError(failureId, e);
        detailStep.setExecutionDate(Reporting.getCurrentDate());
        detailStep.setExecutionTime(Reporting.getCurrentTime());
        detailStep.setRunId(Reporting.getRunId());
        detailStep.setTestCaseName(testCaseData.getTestCaseName());
        detailStep.setModule(testCaseData.getTestCaseModule());
        detailStep.setPageName(pageObject);
        detailStep.setTestStep(stepDescription);
        detailStep.setTestStepStatus(status.name());
        detailStep.setBrowserName(testCaseData.getBrowser());
        detailStep.setFailureId(failureId);
        detailStep.setLocale(testCaseData.getCountry());
        detailStep.setLang(testCaseData.getLanguage());
        detailStep.setDescription(testCaseData.getDescription());
        String OS = System.getProperty("os.name").toLowerCase();
        detailStep.setOs(OS);
        if (status.equals(LogStatus.FAIL) && Reporting.getDriver() != null) {
            failCount++;
            System.out.println(">>>>>>>> FAILED TEST CASE >>>>>>>>>" + testCaseData.getTestCaseName()
                    + ">>>>> EXCEPTION METHOD>>> STATUS >>>>> " + status + " FAIL COUNT: " + failCount);
            detailStep.setScreenshotId(ScreenshotHandler.captureScreenShot(Reporting.getDriver()));
        }
        new CsvWriter().writeDetailCsv(detailStep);
        stopExecution();
    }

    public void logPageLoadTime(LogStatus status, String stepDescription, String pageLoadTime) {
        DetailSteps detailStep = new DetailSteps();

        Long threadId = Thread.currentThread().getId();
//        TestCaseData testCaseData = Reporting.getThreadTCMap().get(threadId);
        TestCaseData testCaseData = Reporting.getThreadTCMap();

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement element = stackTrace[2];
        String className = element.getClassName();
        String pageObject = "";
        if (className.contains("xassure")) {

            element = stackTrace[3];
            className = element.getClassName();
            className = className.substring(className.lastIndexOf("->") + 1);
            pageObject = className + "." + element.getMethodName();
        } else {

            className = element.getClassName();
            className = className.substring(className.lastIndexOf("->") + 1);
            pageObject = className + "." + element.getMethodName();

        }

        detailStep.setRunId(Reporting.getRunId());
        detailStep.setTestCaseName(testCaseData.getTestCaseName());
        detailStep.setPageName(pageObject);
        detailStep.setTestStep(stepDescription);
        detailStep.setTestStepStatus(status.name());
        detailStep.setModule(testCaseData.getTestCaseModule());
        detailStep.setBrowserName(testCaseData.getBrowser());
        detailStep.setPageLoadTime(pageLoadTime);
        detailStep.setExecutionDate(Reporting.getCurrentDate());
        detailStep.setExecutionTime(Reporting.getCurrentTime());
        detailStep.setLocale(testCaseData.getCountry());
        detailStep.setLang(testCaseData.getLanguage());
        detailStep.setDescription(testCaseData.getDescription());
        String OS = System.getProperty("os.name").toLowerCase();
        detailStep.setOs(OS);
        if (status.equals(LogStatus.FAIL) && Reporting.getDriver() != null) {
            detailStep.setScreenshotId(ScreenshotHandler.captureScreenShot(Reporting.getDriver()));
            failCount++;
            System.out.println(">>>>>>>> FAILED TEST CASE >>>>>>>>>" + testCaseData.getTestCaseName()
                    + ">>>>> EXCEPTION METHOD>>> STATUS >>>>> " + status.name() + " FAIL COUNT: " + failCount);
        }
        new CsvWriter().writeDetailCsv(detailStep);
        stopExecution();

    }

    public void log(String locatorTimeJson, String elementName) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement element = stackTrace[4];
        String className = element.getClassName();
        String pageObject = "";
        Locators locator = new Locators();
        if (className.contains("xassure")) {

            element = stackTrace[3];
            className = element.getClassName();
            className = className.substring(className.lastIndexOf("->") + 1);
            pageObject = className + "." + element.getMethodName();
        } else {

            className = element.getClassName();
            pageObject = className.substring(className.lastIndexOf("->") + 1);
//			pageObject = className ;

        }

        Long id = Thread.currentThread().getId();
//        TestCaseData testCaseData = Reporting.getThreadTCMap().get(id);
        TestCaseData testCaseData = Reporting.getThreadTCMap();

        String testCaseName = testCaseData.getTestCaseName();
//        if (testcaseDataMap.containsKey(testCaseName)) {
//            testCaseData.setPageName(pageObject);
//            testcaseDataMap.put(testCaseName, testCaseData);
//        }

        testCaseData.setPageName(pageObject);
        locator.setRunId(Reporting.getRunId());
        locator.setPageName(pageObject);
        locator.setElementName(elementName);
        locator.setLocatorTime(locatorTimeJson);
        locator.setDate(Reporting.getCurrentDate());
        locator.setTime(Reporting.getCurrentTime());

        new CsvWriter().writeLocatorCsv(locator);
        stopExecution();

    }

    private String createLocatorJson(Map<String, String> possibleLocator) {
        String json = "";
        try {

            GsonBuilder gsonMapBuilder = new GsonBuilder().disableHtmlEscaping();

            Gson gsonObject = gsonMapBuilder.create();

            json = gsonObject.toJson(possibleLocator);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    public void log(Map<String, String> possibleLocator, String elementName) {
        DetailSteps detailStep = new DetailSteps();
        String locatorJson = createLocatorJson(possibleLocator);
        Long threadId = Thread.currentThread().getId();
//        TestCaseData testCaseData = Reporting.getThreadTCMap().get(threadId);
        TestCaseData testCaseData = Reporting.getThreadTCMap();

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement element = stackTrace[2];
        String className = element.getClassName();
        String pageObject = "";
        if (className.contains("xassure")) {

            element = stackTrace[3];
            className = element.getClassName();
            className = className.substring(className.lastIndexOf("->") + 1);
            pageObject = className + "." + element.getMethodName();
        } else {

            className = element.getClassName();
            className = className.substring(className.lastIndexOf("->") + 1);
            pageObject = className + "." + element.getMethodName();

        }
        String testCaseName = testCaseData.getTestCaseName();

        detailStep.setExecutionDate(Reporting.getCurrentDate());
        detailStep.setExecutionTime(Reporting.getCurrentTime());
        detailStep.setRunId(Reporting.getRunId());
        detailStep.setModule(testCaseData.getTestCaseModule());
        detailStep.setTestCaseName(testCaseData.getTestCaseName());
        detailStep.setPageName(pageObject);
        detailStep.setTestStep("Possible Locator for the element " + elementName);
        detailStep.setTestStepStatus(LogStatus.INFO.name());
        detailStep.setRemarks(locatorJson);
        detailStep.setBrowserName(testCaseData.getBrowser());

        new CsvWriter().writeDetailCsv(detailStep);
        stopExecution();

    }

    public void logApiResponseTime(LogStatus status, String stepDescription, String apiResponseTime) {
        DetailSteps detailStep = new DetailSteps();

        Long threadId = Thread.currentThread().getId();
//        TestCaseData testCaseData = Reporting.getThreadTCMap().get(threadId);
        TestCaseData testCaseData = Reporting.getThreadTCMap();

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement element = stackTrace[2];
        String className = element.getClassName();
        String pageObject = "";
        if (className.contains("xassure")) {

            element = stackTrace[3];
            className = element.getClassName();
            className = className.substring(className.lastIndexOf("->") + 1);
            pageObject = className + "." + element.getMethodName();
        } else {

            className = element.getClassName();
            className = className.substring(className.lastIndexOf("->") + 1);
            pageObject = className + "." + element.getMethodName();

        }

        detailStep.setRunId(Reporting.getRunId());
        detailStep.setTestCaseName(testCaseData.getTestCaseName());
        detailStep.setModule(testCaseData.getTestCaseModule());
        detailStep.setPageName(pageObject);
        detailStep.setTestStep(stepDescription);
        detailStep.setTestStepStatus(status.name());
        detailStep.setBrowserName(testCaseData.getBrowser());
        detailStep.setApiResponseTime(apiResponseTime);
        detailStep.setExecutionDate(Reporting.getCurrentDate());
        detailStep.setExecutionTime(Reporting.getCurrentTime());
        detailStep.setLocale(testCaseData.getCountry());
        detailStep.setLang(testCaseData.getLanguage());
        detailStep.setDescription(testCaseData.getDescription());
        String OS = System.getProperty("os.name").toLowerCase();
        detailStep.setOs(OS);

        if (status.equals(LogStatus.FAIL) && Reporting.getDriver() != null) {
            detailStep.setScreenshotId(ScreenshotHandler.captureScreenShot(Reporting.getDriver()));
        }
        new CsvWriter().writeDetailCsv(detailStep);
        stopExecution();

    }

    /**
     * Generate Failute ID:-
     */

    public String failureId() {

        // Randomly generates 6 digits unique number.
        Random random = new Random();
        Integer errorId = 1000 + random.nextInt(90000);
        return errorId.toString();
    }

    public void stopExecution() {
        String env = "true";
        try {
            env = System.getProperty("stopExecution");

        } catch (Exception e) {
        }


        if (failCount > 2
                && env.equalsIgnoreCase("true")) {

            throw new RuntimeException("TEST FAILED");
        }
    }

}
