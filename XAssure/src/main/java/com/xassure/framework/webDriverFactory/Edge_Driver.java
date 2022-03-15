package com.xassure.framework.webDriverFactory;

import com.xassure.enums.App;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.util.HashMap;
import java.util.Map;

public class Edge_Driver implements IDriver {

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


        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            System.setProperty("webdriver.edge.driver", App.EDGE_WINDOWS.property

            );
        } else if (System.getProperty("os.name").toLowerCase().equalsIgnoreCase("linux")) {
            System.setProperty("webdriver.edge.driver", App.EDGE_LINUX.property

            );
        }
        WebDriverManager.edgedriver() .setup();

        HashMap<String, Object> edgePrefs = new HashMap<String, Object>();
        edgePrefs.put("profile.default_content_settings.popups", 0);
        EdgeOptions options = new EdgeOptions();
        options.setCapability("prefs", edgePrefs);
        options.setCapability("useAutomationExtension", false);
        driver = new EdgeDriver(options);
        return driver;
    }
}
