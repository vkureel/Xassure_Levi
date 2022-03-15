package com.xassure.framework.webDriverFactory;

import com.xassure.enums.App;
import com.xassure.reporting.utilities.PropertiesFileHandler;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CH_Driver implements IDriver {
    WebDriver driver;

    public static void main(String[] args) {

//        Map<String, String> mobileEmulation = new HashMap<>();
//
//        mobileEmulation.put("deviceName", "Nexus 5");
//        ChromeOptions option = new ChromeOptions();
//        option.setExperimentalOption("mobileEmulation", mobileEmulation);
//        option.addArguments("--disable-gpu");
//        option.addArguments("--disable-dev-shm-usage");
//        option.addArguments("--disable-infobars");
//        option.addArguments("--no-sandbox");
//        option.addArguments("--disable-default-apps");
//        option.addArguments("--allow-insecure-localhost");
//
//        System.setProperty("webdriver.chrome.driver", App.CHROMDRIVER_MAC.property);
//
//        WebDriver driver = new ChromeDriver(option);
////        driver.get("https://sprint-400.levi-site.com/US/en_US?nextgen=true");
//        driver.get("https://www.google.com");
        getDriver();
    }

    public static void takeSnapShot(WebDriver webdriver) throws Exception {

        //Convert web driver object to TakeScreenshot

        TakesScreenshot scrShot = ((TakesScreenshot) webdriver);

        //Call getScreenshotAs method to create image file

        File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);

        //Move image file to new destination

        File DestFile = new File("./levi.jpg");

        //Copy file at destination

        FileUtils.copyFile(SrcFile, DestFile);

    }

    public static void takeSnapShotBase64(WebDriver driver) throws Exception {

        //Convert web driver object to TakeScreenshot

        String base64String = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BASE64);
//        String base64String = "data:image/jpeg;base64,iVBORw0KGgoAAAANSUhEUgAAAHkAAAB5C...";
        System.out.println(base64String);
        String[] strings = base64String.split(",");
        String extension="png";
//        switch (strings[0]) {//check image's extension
//            case "data:image/jpeg;base64":
//                extension = "jpeg";
//                break;
//            case "data:image/png;base64":
//                extension = "png";
//                break;
//            default://should write cases for more images types
//                extension = "jpg";
//                break;
//        }
        //convert base64 string to binary data
        byte[] data = DatatypeConverter.parseBase64Binary(strings[0]);
        String path = "./levi_updated.jpg";
        File file = new File(path);
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getDriver() {
        ChromeOptions option = new ChromeOptions();
        String headLessExecution = "false";
//        Map<String, String> mobileEmulation;
//        String deviceName;
//        if (params.length == 1) {
//            headLessExecution = params[0];
//        }
//        if (params.length == 2) {
//
//            headLessExecution = params[0].trim();
//            deviceName = params[1].trim();
//            mobileEmulation = new HashMap<>();
//            mobileEmulation.put("deviceName", deviceName);
//            option.setExperimentalOption("mobileEmulation", mobileEmulation);
//
//        }

        String headless = new PropertiesFileHandler().readProperty("setupConfig", "windowSize");

        if (!headless.isEmpty()) {
            String windowsize = "--window-size=" + headless.trim();
            option.addArguments(windowsize);
        }
        //Set the setHeadless is equal to true which will run test in Headless mode
        option.setHeadless(Boolean.parseBoolean(headLessExecution));
        option.addArguments("--disable-gpu");
        option.addArguments("--disable-dev-shm-usage");
        option.addArguments("--disable-infobars");
        option.addArguments("--no-sandbox");
        option.addArguments("--disable-default-apps");
        option.addArguments("--allow-insecure-localhost");

        option.setAcceptInsecureCerts(true);
        option.setAcceptInsecureCerts(true);

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors," +
                "--web-security=false,--ssl-protocol=any,--ignore-ssl-errors=true"));

//        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
//            System.setProperty("webdriver.chrome.driver", App.CHROMEDRIVER_WINDOWS.property);
//        } else if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
//            System.setProperty("webdriver.chrome.driver", App.CHROMDRIVER_MAC.property);
//        }else if (System.getProperty("os.name").equalsIgnoreCase("linux")) {
//            System.setProperty("webdriver.chrome.driver", App.CHROMDRIVER_LINUX.property);
//        }
        WebDriverManager.chromedriver().setup();
        try {

            WebDriver driver = new ChromeDriver(option);
            driver.get("https://staging01.dtc.levi.com/US/en_US/");
            driver.manage().window().maximize();
            Thread.sleep(2000);
            takeSnapShot(driver);
            takeSnapShotBase64(driver);
            driver.quit();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public WebDriver getDriver(String... params) {
        ChromeOptions option = new ChromeOptions();
        String headLessExecution = "false";
        Map<String, String> mobileEmulation;
        String deviceName;
        if (params.length == 1) {
            headLessExecution = params[0];
        }
        if (params.length == 2) {

            headLessExecution = params[0].trim();
            deviceName = params[1].trim();
            mobileEmulation = new HashMap<>();
            mobileEmulation.put("deviceName", deviceName);
            option.setExperimentalOption("mobileEmulation", mobileEmulation);

        }
        String headless = new PropertiesFileHandler().readProperty("setupConfig", "windowSize");

        if (headless != null && !headless.isEmpty()) {
            String windowsize = "--window-size=" + headless.trim();
            option.addArguments(windowsize);
        }

        //Set the setHeadless is equal to true which will run test in Headless mode
        option.setHeadless(Boolean.parseBoolean(headLessExecution));
        option.addArguments("--disable-gpu");
        option.addArguments("--disable-dev-shm-usage");
        option.addArguments("--disable-infobars");
        option.addArguments("--no-sandbox");
        option.addArguments("--disable-default-apps");
        option.addArguments("--allow-insecure-localhost");

        option.setAcceptInsecureCerts(true);
        option.setAcceptInsecureCerts(true);

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors," +
                "--web-security=false,--ssl-protocol=any,--ignore-ssl-errors=true"));

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            System.setProperty("webdriver.chrome.driver", App.CHROMEDRIVER_WINDOWS.property);
        } else if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
            System.setProperty("webdriver.chrome.driver", App.CHROMDRIVER_MAC.property);
        } else if (System.getProperty("os.name").equalsIgnoreCase("linux")) {
            System.setProperty("webdriver.chrome.driver", App.CHROMDRIVER_LINUX.property);
        }
        WebDriverManager.chromedriver().setup();

        try {
//            driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), option);
            driver = new ChromeDriver(option);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return driver;
    }
}
