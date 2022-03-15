package com.xassure.galen;

import com.galenframework.api.Galen;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.HtmlReportBuilder;
import com.galenframework.reports.model.LayoutReport;
import com.xassure.reporting.logger.LogStatus;
import com.xassure.reporting.logger.Reporting;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.List;

public class GalenDriver {

    public GalenTestInfo verifyUI(WebDriver driver, String specFile) {
        GalenTestInfo test = null;
        try {
            String page = specFile.split("\\.")[0];
            LayoutReport layoutReport = Galen.checkLayout(driver, "src/main/resources/specsFolder/" + specFile,
                    Arrays.asList("desktop"));

            // Create a GalenTestInfo object
            test = GalenTestInfo.fromString(page + " layout");

            // Get layoutReport and assign to test object
            test.getReport().layout(layoutReport, "check " + page + " layout");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return test;
    }

    public void generateReport(List<GalenTestInfo> tests) {
        try {
            // Create a htmlReportBuilder object
            HtmlReportBuilder htmlReportBuilder = new HtmlReportBuilder();

            // Create a report under /target folder based on tests list
            htmlReportBuilder.build(tests, "target");

            Reporting.getLogger().log(LogStatus.INFO,
                    "UI test report: " + System.getProperty("user.dir") + "/target/report.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
    }
}
