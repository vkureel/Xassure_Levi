package com.xassure.framework.listeners;

import com.xassure.reporting.logger.LogStatus;
import com.xassure.reporting.logger.Reporting;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class Listener implements ITestListener {


    @Override
    public void onTestStart(ITestResult result) {

    }

    @Override
    public void onTestSuccess(ITestResult result) {

    }

    @Override
    public void onTestFailure(ITestResult result) {
        Reporting.getLogger().log(LogStatus.FAIL, result.getName() + " failed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Reporting.getLogger().log(LogStatus.FAIL, result.getName() + " failed");

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {

    }
}
