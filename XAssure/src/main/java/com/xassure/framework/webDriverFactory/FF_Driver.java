package com.xassure.framework.webDriverFactory;

//import io.github.bonigarcia.wdm.WebDriverManager;
import com.xassure.enums.App;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Map;

public class FF_Driver implements IDriver {

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
            throw new RuntimeException("mobile Emulation is not enabled for firefox");
        }


        // Create Object of ChromeOption Class
        FirefoxOptions option = new FirefoxOptions();
        //Set the setHeadless is equal to true which will run test in Headless mode
        option.setHeadless(Boolean.parseBoolean(headLessExecution));
        option.addPreference("javascript.enabled", true);
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            System.setProperty("webdriver.gecko.driver", App.GECKODRIVER_WINDOWS.property);
        } else if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
            System.setProperty("webdriver.gecko.driver", App.GECKODRIVER_MAC.property);
        }else if (System.getProperty("os.name").equalsIgnoreCase("Linux")) {
            System.setProperty("webdriver.gecko.driver", App.GECKODRIVER_LINUX.property);
        }
//        WebDriverManager.firefoxdriver().setup();

        try {
            driver = new FirefoxDriver(option);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return driver;
    }
}
