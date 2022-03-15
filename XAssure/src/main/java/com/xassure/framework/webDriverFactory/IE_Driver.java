package com.xassure.framework.webDriverFactory;

import com.xassure.enums.App;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;

import java.util.Map;

public class IE_Driver implements IDriver {

    private WebDriver driver;

    public WebDriver getDriver(String... params) {

        String headLessExecution = "false";
        Map<String, String> mobileEmulation;
        String deviceName;
        if (params.length == 1) {
            headLessExecution = params[0];
        }
        if (params.length == 2) {

//            headLessExecution = params[0].trim();
//            deviceName = params[1].trim();
//            mobileEmulation = new HashMap<>();
//            mobileEmulation.put("deviceName", deviceName);
            throw new RuntimeException("mobile Emulation is not enabled for IE");
        }


//        // Create Object of ChromeOption Class
//        FirefoxOptions option = new FirefoxOptions();
//        //Set the setHeadless is equal to true which will run test in Headless mode
//        option.setHeadless(Boolean.parseBoolean(headLessExecution));
//        option.addPreference("javascript.enabled", true);

//        try {
//            driver = new FirefoxDriver(option);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            System.setProperty("webdriver.ie.driver", App.IEDRIVER_WINDOWS.property

            );
        }
        InternetExplorerOptions internetExplorerOptions = new InternetExplorerOptions();
        internetExplorerOptions.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
//        internetExplorerOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
        internetExplorerOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        internetExplorerOptions.introduceFlakinessByIgnoringSecurityDomains();
        internetExplorerOptions.requireWindowFocus();
        driver = new InternetExplorerDriver(internetExplorerOptions);
        return driver;
    }
}
