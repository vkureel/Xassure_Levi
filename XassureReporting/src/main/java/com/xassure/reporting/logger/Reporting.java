package com.xassure.reporting.logger;

import com.xassure.reporting.beans.Setup;
import com.xassure.reporting.csvHandlers.CsvWriter;
import com.xassure.reporting.testCaseDetails.TestCaseData;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Reporting {

    volatile static ThreadLocal<Logger> reportingLogger = new ThreadLocal();
    volatile static ThreadLocal<TestCaseData> testCaseDataThreadLocal = new ThreadLocal<>();
    private static String reportLocation = "./reports";
    private static String detailCsvLoc;
    private static String locatorCsvLoc;
    private static String errorJson;
    private static String setupCsvLoc;
    private static String screenshotLoc;
    private static String runId;
    private static String executionStartTime;
    private static String executionFinishTime;
    private static boolean captureScreenShotAtEveryStepFlag;
    private static Map<Long, WebDriver> threadDriver = new HashMap();
    private static Map<String, Integer> TC_COUNTER = new HashMap<>();
    private static volatile List<Logger> loggerList = new ArrayList<>();
    private static WebDriver driver;
    private Logger _reporting;

    public static String getErrorJson() {
        return errorJson;
    }

    public static List<Logger> getLoggerList() {

        return loggerList;
    }

    public static TestCaseData getThreadTCMap() {
        return reportingLogger.get().getTestCaseData();

    }

    public static String getRunId() {
        return runId;
    }

    public static WebDriver getDriver() {
        long id = Thread.currentThread().getId();
        return threadDriver.get(id);
    }

    public static void setDriver(WebDriver driver) {
        long id = Thread.currentThread().getId();
        threadDriver.put(id, driver);
    }

    public static String getDetailCsvLoc() {
        return detailCsvLoc;
    }

    public static String getLocatorCsvLoc() {
        return locatorCsvLoc;
    }

    public void setLocatorCsvLoc(String locatorCsvLoc) {
        this.locatorCsvLoc = locatorCsvLoc;
    }

    public static String getSetupCsvLoc() {
        return setupCsvLoc;
    }

    public void setSetupCsvLoc(String setupCsvLoc) {
        this.setupCsvLoc = setupCsvLoc;
    }

    public static String getScreenshotLoc() {
        return screenshotLoc;
    }

    /**
     * Logger
     */
    public static synchronized Logger getLogger() {

        return reportingLogger.get();
    }

    public static String getExecutionStartTime() {
        return executionStartTime;
    }

    public static void setExecutionFinishTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));

        executionFinishTime = DateFormatUtils.format(calendar.getTime(), "dd:MM:yy hh:mm:ss a");
//        executionFinishTime = DateFormatUtils.format(new Date().getTime(), "hh:mm:ss a");

    }

    public static String getExecutionFinishTime() {
        return executionFinishTime;
    }

    public static String getExecutionTime() {

        String timeDiff = null;
        try {

            // String time2 = "12:01:00";

            SimpleDateFormat format = new SimpleDateFormat("dd:MM:yy hh:mm:ss a");
            Date date1 = format.parse(executionStartTime);
            Date date2 = format.parse(executionFinishTime);
            long difference = (date2.getTime() - date1.getTime()) / 1000;
            long hours = difference / 3600;
            long minutes = (difference % 3600) / 60;
            long seconds = difference % 60;
            timeDiff = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timeDiff;

    }

    public static String getCurrentDate() {
        return DateFormatUtils.format(new Date(), "dd/MM/yyyy");
    }

    public static String getCurrentTime() {

        return DateFormatUtils.format(new Date().getTime(), "hh:mm:ss");

    }

    public static void main(String[] args) throws Exception{
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));

        String executiontime = DateFormatUtils.format(calendar.getTime(), "dd:MM:yy hh:mm:ss a");
        System.out.println(executiontime);

        SimpleDateFormat format = new SimpleDateFormat("dd:MM:yy hh:mm:ss a");
        Date date1 = format.parse("26:02:21 12:33:08 PM");
        Date date2 = format.parse("27:02:21 12:33:08 AM");
        long difference = (date2.getTime() - date1.getTime()) / 1000;
        long hours = difference / 3600;
        long minutes = (difference % 3600) / 60;
        long seconds = difference % 60;
        String timeDiff = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        System.out.println(timeDiff);
    }

    /**
     * Start Reporting for a particular test case:-
     *
     * @param testCaseData
     * @return
     */
    public synchronized Logger startReporter(
            TestCaseData testCaseData) {

        reportingLogger.set(new Logger(testCaseData));
        testCaseDataThreadLocal.set(testCaseData);
        loggerList.add(reportingLogger.get());
        return this._reporting;

    }

    /**
     * On each call of this function, it randomly generates a unique run id
     * (combination of 6 digits).
     *
     * @return String runId
     */
    public String generateUniqueRunId() {
        String runID = "";
        try {
            // Randomly generates 6 digits unique number.
            Random random = new Random();
            int n = 100000 + random.nextInt(900000);
            runID = String.valueOf(n);

            // Set 'runID' value into the runtime hashmap.
            // InfoLogger.setRuntimeTempData("runId" + thread_Id, runID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return runID.trim();
    }

    private void setExecutionStartTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));

        executionStartTime = DateFormatUtils.format(calendar.getTime(), "dd:MM:yy hh:mm:ss a");
//        executionStartTime = DateFormatUtils.format(new Date().getTime(), "hh:mm:ss a");
    }

    /**
     * Generate Folder architecture:-
     */
    public void createFolder(String suiteName, boolean captureScreenShotAtEveryStep,
                             String releaseId, String sprintId, String testsuiteId) {
        captureScreenShotAtEveryStepFlag = captureScreenShotAtEveryStep;
        runId = generateUniqueRunId();
        System.out.println("Run Id for this execution is: " + runId);
        createReportDirectory(suiteName, releaseId, sprintId, testsuiteId);
        setExecutionStartTime();
    }

    /**
     * Generates a unique folder based upon our run:-
     */
    private void createReportDirectory(String suiteName, String releaseId, String sprintId, String suiteId) {

        CsvWriter csvWriter = new CsvWriter();
        reportLocation = reportLocation + "/" + runId + "/";
        screenshotLoc = reportLocation + "screenshots";
        errorJson = reportLocation + "error.json";
        File file = new File(reportLocation);

        // Create report folder if they doesn't exists:-
        if (!file.exists()) {
            if (!file.exists()) {
                if (file.mkdirs()) {
                    createReportsSubFolder();
                } else {
                    System.out.println("Failed to create directory!");
                }
            }

        } else
            createReportsSubFolder();

        // Create .csv file:-
        this.detailCsvLoc = reportLocation + "DetailedReport.csv";
        csvWriter.createDetailCsv(detailCsvLoc);

        // Create setup.csv file:-
        this.setupCsvLoc = reportLocation + "Setup.csv";
        csvWriter.createSetupCsv(setupCsvLoc);

        // Crete locatortime.csv file
        this.locatorCsvLoc = reportLocation + "LocatorTime.csv";
        csvWriter.createLocatorCsv(locatorCsvLoc);
        setExecutionStartTime();

        Setup setupData = new Setup();
        setupData.setRunId(runId);
        setupData.setTestCaseCount("0");
        setupData.setSuiteName(suiteName);
        setupData.setExecutionStatus("In Progress");
        setupData.setExecutionStartTime(executionStartTime);
        setupData.setExecutionEndTime(executionStartTime);
        setupData.setSprintId(sprintId);
        setupData.setReleaseId(releaseId);
        setupData.setSuitId(suiteId);
        setupData.setApplicationType("web");

        String env = System.getProperty("environment");
        String buildNumber = System.getProperty("buildNumber");
        String brand = System.getProperty("app");
        setupData.setEnvironment(env);
        setupData.setBuildNumber(buildNumber);
        setupData.setBrand(brand);
        csvWriter.writeSetupCsv(setupData);

    }

    /**
     * Create reports subFolder:-
     */
    private void createReportsSubFolder() {

        // create screenshots folder:-
        if (!screenshotLoc.isEmpty()) {
            File screenshot = new File(screenshotLoc);
            if (!screenshot.exists()) {
                if (!screenshot.mkdir()) {

                    System.out.print("Unable to create error logs directory for reportlocation: " + reportLocation);
                }
            }
        }

    }
}
