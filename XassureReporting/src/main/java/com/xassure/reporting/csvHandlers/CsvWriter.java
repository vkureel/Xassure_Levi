package com.xassure.reporting.csvHandlers;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.xassure.reporting.beans.DetailSteps;
import com.xassure.reporting.beans.Locators;
import com.xassure.reporting.beans.Setup;
import com.xassure.reporting.logger.Reporting;
import com.xassure.reporting.utilities.EmailReport;

public class CsvWriter {

    // defining header for detailed CSV
    final String[] detailCsvHeader = new String[]{"RunId", "Module", "TestCaseName", "PageName", "TestStep",
            "TestStepStatus", "BrowserName", "PageLoadTime", "ApiResponseTime", "Remarks", "FailureId", "ScreenshotId",
            "ExecutionDate", "ExecutionTime", "locale", "lang", "description","os"};

    // defining headers for setup CSV
    final String[] setupCsvHeader =
            new String[]{
                    "RunId",
                    "SuiteName",
                    "ExecutionStartTime",
                    "ExecutionEndTime",
                    "TestCaseCount",
                    "ExecutionStatus",
                    "SuitId",
                    "ReleaseId",
                    "SprintId",
                    "environment",
                    "buildNumber",
                    "brand",
                    "ApplicationType"
            };

    // defining headers for locator CSV
    final String[] locatorCsvHeader = new String[]{"RunId", "PageName", "ElementName", "LocatorTime", "Date",
            "Time"};

    /**
     * Sets up the processor for the rows of the setup CSV
     */
    private static CellProcessor[] getSetupCsvProcessors() {
        final CellProcessor[] processors = new CellProcessor[]{
                new NotNull(), // runId
                new NotNull(), // SuiteName
                new NotNull(), // StartTime
                new NotNull(), // EndTime
                new NotNull(), // TestCount
                new NotNull(), // ExecutionStatus
                new NotNull(), // ReleaseID
                new NotNull(), // SprintId
                new NotNull(), // TestSuitId
                new NotNull(), // environment
                new NotNull(), // buildNumber
                new NotNull(), // buildNumber
                new NotNull() // brand
        };
        return processors;
    }

    /**
     * Sets up the processors for the rows of the detail CSV
     */
    private static CellProcessor[] getDetailCsvProcessors() {

        final CellProcessor[] processors = new CellProcessor[]{new NotNull(), // runId
                new NotNull(), // TestCasename
                new NotNull(), // Module Name
                new Optional(), // PageName
                new NotNull(), // TestStep
                new NotNull(), // Status
                new Optional(), // BrowserName
                new Optional(), // PageLoadTime
                new Optional(), // ApiResponseTime
                new Optional(), // MultiLocatorId
                new Optional(), // FailureId
                new Optional(), // ScreenshotID
                new NotNull(), // ExecutionDate
                new NotNull(),// ExecutionTime
                new NotNull(),// locale
                new NotNull(),// lang
                new NotNull(),// description
                new NotNull(),// description

        };
        return processors;
    }

    /**
     * Sets up the processor for the rows of the locator CSV
     */
    private static CellProcessor[] getLocatorCsvProcessors() {
        final CellProcessor[] processors = new CellProcessor[]{new NotNull(), // RunId
                new NotNull(), // PageName
                new NotNull(), // LocatorTime
                new NotNull(), // ElementName
                new NotNull(), // Date
                new NotNull()// Time

        };
        return processors;
    }

    /**
     * Create detail CSV file with headers
     */
    public void createDetailCsv(String detailCsv) {
        ICsvBeanWriter beanWriter = null;
        try {
            beanWriter = new CsvBeanWriter(new FileWriter(detailCsv), CsvPreference.STANDARD_PREFERENCE);

            beanWriter.writeHeader(detailCsvHeader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                beanWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create setup CSV file with headers
     */
    public void createSetupCsv(String setupCsv) {
        ICsvBeanWriter beanWriter = null;
        try {
            beanWriter = new CsvBeanWriter(new FileWriter(setupCsv), CsvPreference.STANDARD_PREFERENCE);

            beanWriter.writeHeader(setupCsvHeader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                beanWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create Locator CSV file with headers
     */
    public void createLocatorCsv(String locatorCsv) {
        ICsvBeanWriter beanWriter = null;
        try {
            beanWriter = new CsvBeanWriter(new FileWriter(locatorCsv), CsvPreference.STANDARD_PREFERENCE);

            beanWriter.writeHeader(locatorCsvHeader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                beanWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write rows into detail CSV file
     *
     * @param detailsStep
     */
    public void writeDetailCsv(DetailSteps detailsStep) {
        ICsvBeanWriter beanWriter = null;

        try {

            beanWriter = new CsvBeanWriter(new FileWriter(Reporting.getDetailCsvLoc(), true),
                    CsvPreference.STANDARD_PREFERENCE);

            final CellProcessor[] processors = getDetailCsvProcessors();
            beanWriter.write(detailsStep, detailCsvHeader, processors);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                beanWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes data into Setup CSV file
     *
     * @param setupData
     */
    public void writeSetupCsv(Setup setupData) {
        ICsvBeanWriter beanWriter = null;

        try {

            beanWriter = new CsvBeanWriter(new FileWriter(Reporting.getSetupCsvLoc(), true),
                    CsvPreference.STANDARD_PREFERENCE);

            final CellProcessor[] processors = getSetupCsvProcessors();
            beanWriter.write(setupData, setupCsvHeader, processors);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                beanWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes data into Locator CSV file
     *
     * @param locatorData
     */
    public void writeLocatorCsv(Locators locatorData) {
        ICsvBeanWriter beanWriter = null;

        try {

            beanWriter = new CsvBeanWriter(new FileWriter(Reporting.getLocatorCsvLoc(), true),
                    CsvPreference.STANDARD_PREFERENCE);

            final CellProcessor[] processors = getLocatorCsvProcessors();
            beanWriter.write(locatorData, locatorCsvHeader, processors);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                beanWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateSetupCsv(int totalTestCaseCount) {
        ICsvBeanReader beanReader = null;

        try {
            beanReader = new CsvBeanReader(new FileReader(Reporting.getSetupCsvLoc()),
                    CsvPreference.STANDARD_PREFERENCE);
            Setup setupData;
            int count = 0;
            while ((setupData = beanReader.read(Setup.class, setupCsvHeader)) != null) {
                count++;
                if (count == 2) {
                    break;
                }
            }
            setupData.setExecutionEndTime(Reporting.getExecutionFinishTime());
            setupData.setApplicationType("web");
            setupData.setTestCaseCount(totalTestCaseCount + "");
            if (new EmailReport().getSkippedTestCaseCount() == 0) {
                setupData.setExecutionStatus("Complete");
            } else {
                setupData.setExecutionStatus("Skipped");
            }

            createSetupCsv(Reporting.getSetupCsvLoc());
            writeSetupCsv(setupData);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
