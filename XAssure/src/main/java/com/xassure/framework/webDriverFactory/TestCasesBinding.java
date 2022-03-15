package com.xassure.framework.webDriverFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.xassure.reporting.testCaseDetails.TestCase;
import com.xassure.reporting.testCaseDetails.TestCaseData;

public class TestCasesBinding extends AbstractModule {

    private TestCaseData testCaseData;

    public TestCasesBinding(TestCaseData testCaseData) {
        this.testCaseData = testCaseData;
    }

    @Override
    public void configure() {

    }

    @Provides
    public TestCaseData getTestCaseData() {
        return this.testCaseData;
    }
}
