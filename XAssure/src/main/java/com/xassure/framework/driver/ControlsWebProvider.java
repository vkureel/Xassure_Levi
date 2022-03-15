package com.xassure.framework.driver;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.xassure.framework.webDriverFactory.DriverBinding;
import com.xassure.reporting.testCaseDetails.TestCaseData;
import com.xassure.reporting.utilities.PropertiesFileHandler;

import java.util.HashMap;
import java.util.Map;

public class ControlsWebProvider implements Provider<Controls> {
    public static volatile Map<String, Controls> webDriverMap;
    public String headLessExecution;
    volatile public TestCaseData testCaseData;
    ThreadLocal<Controls> ControlsInfo = new ThreadLocal<>();
    PropertiesFileHandler _propHandler;

    @Inject
    public ControlsWebProvider(TestCaseData testCaseData) {

        this.testCaseData = testCaseData;
        headLessExecution = new PropertiesFileHandler().readProperty("setupConfig", "headLessExecution");
        System.out.println("In controls web provider testcase config value: " + testCaseData.toString());
    }

    public synchronized static void removeThread() {
        String threadId = Thread.currentThread().getId() + "";
        webDriverMap.remove(threadId);
    }

    @Override
    public synchronized Controls get() {
        if (webDriverMap == null) {
            webDriverMap = new HashMap<>();
        }
        String threadId = Thread.currentThread().getId() + "";
        if (ControlsInfo.get() == null) {
            if (!(webDriverMap.containsKey(threadId))
                    || ((!testCaseData.isExecutionStarted())
            )) {
                _propHandler = new PropertiesFileHandler();
                Injector injectorDriver = Guice.createInjector(new DriverBinding());
                Controls webLibrary = injectorDriver.getInstance(WebLibrary.class);
                ControlsInfo.set(webLibrary);
                System.out.println("Value of thread id in driver class " + threadId + " Browser Details: " +
                        testCaseData.getBrowser() + " Locale Val: " + testCaseData.getLocale());

                String browser = testCaseData.getBrowser();
                String configname = testCaseData.getConfig();
                String testCaseName = testCaseData.getTestCaseName();
                System.out.println("TEST CASE NAME in CONTORLS WEB PROVIDER: " + testCaseName);
                ControlsInfo.get().setDriver(browser, headLessExecution, configname, testCaseName);
                webDriverMap.put(threadId, ControlsInfo.get());
                testCaseData.setExecutionStarted(true);
            }
        }
        return webDriverMap.get(threadId);
    }

}
