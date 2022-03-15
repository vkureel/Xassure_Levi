package com.xassure.framework.driver;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.xassure.enums.Drivers;
import com.xassure.framework.webDriverFactory.IDriver;
import com.xassure.reporting.logger.LogStatus;
import com.xassure.reporting.logger.Reporting;
import com.xassure.reporting.utilities.PropertiesFileHandler;
import io.appium.java_client.TouchAction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WebLibrary implements Controls {
    private static String errorMsg = null;
    PageInfo pageInfo;
    Duration pollingDuration = Duration.of(250, ChronoUnit.MILLIS);
    WebDriver driver;
    String timeLocatorJson = null;
    JSONObject jsonObj;
    JSONArray jsonArray;
    Duration timeOutDuration = Duration.of(10, ChronoUnit.SECONDS);
    Map<String, Object> pageTestData;

    @Inject
    Map<String, Provider<IDriver>> iMap;
    private boolean locatorTimeFlag = Boolean
            .parseBoolean((new PropertiesFileHandler()).readProperty("setupConfig", "captureLocatorTime"));

    private boolean locatorSuggestionFlag = Boolean
            .parseBoolean((new PropertiesFileHandler()).readProperty("setupConfig", "newLocatorCapture"));


    @Override
    public PageInfo getPageDetails() {
        return pageInfo;
    }

    @Override
    public void setPageDetails(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public WebDriver getDriver() {
        if (driver != null) {
            return driver;
        }
        return null;
    }

    public void setDriver(String... params) {

//        if (params.length == 1) {
//
//            browser = params[0];
//            this.driver = iMap.get(browser.toUpperCase()).get().getDriver(browser);
//
//        } else if (params.length == 2) {
//
//            browser = params[0];
//            headlessExecution = params[1];
//            this.driver = iMap.get(browser.toUpperCase()).get().getDriver(headlessExecution);
//
//        } else if (params.length == 3) {
//            browser = params[0];
//            headlessExecution = params[1];
//            deviceType = params[2];
//            this.driver = iMap.get(browser.toUpperCase()).get().getDriver(headlessExecution, deviceType);
//
//        }


        String browser = params[0];
        String headlessExecution = params[1];
        String config = params[2];
        String testCaseName = params[3];
        if (config != null) {
            if (browser.equalsIgnoreCase(Drivers.BROWSERSTACK.driver)) {
                this.driver = iMap.get(browser.toUpperCase()).get().getDriver(config, testCaseName);
            } else if (browser.equalsIgnoreCase(Drivers.EXPERITEST.driver)) {
                this.driver = iMap.get(browser.toUpperCase()).get().getDriver(config, testCaseName);
            } else if (browser.equalsIgnoreCase(Drivers.CHROME.driver)) {

                this.driver = iMap.get(browser.toUpperCase()).get().getDriver(headlessExecution, config);
            }
        } else {
            this.driver = iMap.get(browser.toUpperCase()).get().getDriver(headlessExecution);
        }
//        this.driver.manage().window().maximize();
        this.driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        Reporting.setDriver(driver);

        Long threadId = Thread.currentThread().getId();
        System.out.println("Value of thread id in driver class " + threadId + " Browser Details: " +
                browser);
    }

    public void launchApplication(String url) {
        driver.get(url);
        String currentUrl = driver.getCurrentUrl();
        Reporting.getLogger().log(LogStatus.INFO, "URL '" + url + "' is succesfully launched");

//        if (currentUrl.toLowerCase().contains(url.toLowerCase())) {
//        } else if (currentUrl.isEmpty() && !url.isEmpty()) {
//            Reporting.getLogger().log(LogStatus.PASS, "Failed to launch URL '" + url + "'");
//        }
    }

    public boolean click(String elementName, String property) {
        boolean bFlag = false;
        List<WebElement> elements = getWebElementList(property, elementName);
        try {
            for (WebElement element : elements) {
                WebElement ele = verifyElementClickable(element);
                if (ele != null) {
                    highlighElement(ele);
                    ele.click();
                    bFlag = true;

                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Failed to click on Element '" + elementName + "'.");
            } else {
                Reporting.getLogger().log(LogStatus.PASS, "Click on Element '" + elementName + "'.");
            }
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while clicking on element '" + elementName + "'", e);
            e.printStackTrace();
        }

        return bFlag;
    }

    public boolean clickUsingActionBuilder(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                WebElement ele = verifyElementClickable(element);
                if (ele != null) {

                    highlighElement(ele);
                    Actions action = new Actions(getDriver());
                    action.click(ele).build().perform();
                    bFlag = true;

                    break;
                }
            }

            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL,
                        "Failed to click on Element '" + elementName + "' using action builder.");

            }

        } catch (TimeoutException e) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Timeout Exception occured while clicking on element '" + elementName + "' using action builder",
                    e);

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while clicking on element '" + elementName + "' using action builder", e);
        }

        return bFlag;
    }

    public boolean clickUsingJavaScriptExecutor(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);
            JavascriptExecutor jsExec = (JavascriptExecutor) getDriver();
            for (WebElement element : elements) {
                WebElement ele = verifyElementClickable(element);
                if (ele != null) {
                    highlighElement(ele);
                    try {
                        jsExec.executeScript("arguments[0].scrollIntoView({block: \"center\"});", ele);
                    } catch (Exception e) {

                    }
                    jsExec.executeScript("arguments[0].click();", ele);
                    bFlag = true;

                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL,
                        "Failed to click on Element '" + elementName + "' using javascript executor.");

            }

        } catch (TimeoutException e) {
            Reporting.getLogger().log(LogStatus.INFO, "Timeout Exception occurred while clicking on element '"
                    + elementName + "' using javascript executor", e);
        } catch (StaleElementReferenceException e) {
            Reporting.getLogger().log(LogStatus.INFO, "Stale Element Reference Exception occurred while clicking on element '"
                    + elementName + "' using javascript executor", e);
        } catch (ElementClickInterceptedException e) {
            Reporting.getLogger().log(LogStatus.INFO, "Stale Element Reference Exception occurred while clicking on element '"
                    + elementName + "' using javascript executor", e);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while clicking on element '" + elementName + "' using javascript executor", e);
        }

        return bFlag;
    }

    public void enterText(String elementName, String property, String textToEnter) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);
            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {
                    element.clear();
                    highlighElement(element);
                    scrolltoCenterElement(element);
                    element.sendKeys(textToEnter);
                    bFlag = true;
                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL,
                        "Text '" + textToEnter + "' not entered successfuly into '" + elementName);

            } else {
                Reporting.getLogger().log(LogStatus.PASS,
                        "Text '" + textToEnter + "' entered successfuly into '" + elementName);

            }

        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Timeout Exception occured while entering text : '" + textToEnter + "' into '" + elementName,
                    t);

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while entering text : '" + textToEnter + "' into '" + elementName, e);
        }
    }

    public void enterText(String elementName, String property, Keys keys) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);
            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {
                    element.clear();
                    highlighElement(element);
                    scrolltoCenterElement(element);
                    element.sendKeys(keys);
                    bFlag = true;
                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL,
                        "Keys '" + keys.name() + "' didn't work for an element: '" + elementName);

            } else {
                Reporting.getLogger().log(LogStatus.PASS,
                        "Keys '" + keys.name() + "' worked for an element: '" + elementName);

            }

        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Timeout Exception occurred while entering keys : '" + keys.name() + "' into '" + elementName,
                    t);

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while entering keys : '" + keys.name() + "' into '" + elementName, e);
        }
    }

    public void enterTextUsingJavascriptExecutor(String elementName, String property, String textToEnter) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);
            JavascriptExecutor jsExec = (JavascriptExecutor) getDriver();
            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {


                    try {
                        jsExec.executeScript("arguments[0].scrollIntoView({block: \"center\"});", element);
                    } catch (Exception e) {

                    }
                    jsExec.executeScript("arguments[0].value='" + textToEnter + "'", element);
                    bFlag = true;
                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL,
                        "Text '" + textToEnter + "' not entered successfuly into '" + elementName);

            } else {
                Reporting.getLogger().log(LogStatus.PASS,
                        "Text '" + textToEnter + "' entered successfuly into '" + elementName);

            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Timeout Exception occured while entering text : '" + textToEnter + "' into '" + elementName,
                    t);

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while entering text : '" + textToEnter + "' into '" + elementName, e);
        }
    }

    public boolean clearTextBox(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);
            for (WebElement element : elements) {
                if (visibilityofWebelement(element)) {

                    element.clear();
                    bFlag = true;
                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Failed to clear text from " + elementName);
            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception on clear text from " + elementName,
                    t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception on clear text from " + elementName, e);
        }
        return bFlag;
    }

    public boolean isTextBoxEditable(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {
                    element.sendKeys("test");
                    element.clear();
                    bFlag = true;
                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Text box '" + elementName + "' is not editable");
            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Timeout Exception while checking Text box is editable for'" + elementName, t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while checking Text box is editable for'" + elementName, e);
        }

        return bFlag;
    }

    public JavascriptExecutor getJavaScriptExecutor() {
        return (JavascriptExecutor) getDriver();
    }

    public void executeJavaScript(String javaScript) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript(javaScript);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while executing javascript on browser", e);
        }
    }

    public void selectDropdownWithIndex(String elementName, String property, int index) {
        boolean bFlag = false;

        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {

                    highlighElement(element);
                    Select dropDown = new Select(element);
                    dropDown.selectByIndex(index);
                    bFlag = true;
                    break;
                }
            }
            if (bFlag) {

                Reporting.getLogger().log(LogStatus.PASS, "Successfuly selected option for dropdown '" + elementName);
            } else {

                Reporting.getLogger().log(LogStatus.FAIL, "Failed to selected option for dropdown '" + elementName);
            }

        } catch (TimeoutException timeoutException) {

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while selecting option for dropdown '" + elementName, e);
        }
    }

    public boolean selectDropdownWithVisibleText(String elementName, String property, String visibleText) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);
            for (WebElement element : elements) {
                if (visibilityofWebelement(element)) {
                    highlighElement(element);
                    Select dropDown = new Select(element);
                    dropDown.selectByVisibleText(visibleText);
                    bFlag = true;
                    break;
                }
            }
            if (bFlag) {
                Reporting.getLogger().log(LogStatus.PASS,
                        "Successfuly selected option '" + visibleText + "' from dropdown '" + elementName);
            } else {

                Reporting.getLogger().log(LogStatus.FAIL,
                        "Failed to selected option '" + visibleText + "' from dropdown '" + elementName);
            }

        } catch (TimeoutException timeoutException) {

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while selecting option '" + visibleText + "' from dropdown '" + elementName);
        }

        return bFlag;
    }

    public String getDropdownSelectedVisibleText(String elementName, String property) {
        String text = "";
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {
                    Select dropDown = new Select(element);
                    List<WebElement> selectedElements = dropDown.getAllSelectedOptions();
                    for (WebElement elem : selectedElements) {

                        if (elem.isSelected() == true) {

                            text = elem.getText();

                            break;
                        }
                    }
                    bFlag = true;

                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL,
                        "Unable to get Dropdown visible text for element " + elementName);

            }
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while finding the dropdown selected visible text for element " + elementName, e);
        }

        return text;
    }

    public List<String> getDropdownOptions(String elementName, String property) {
        boolean bFlag = false;
        List<String> dropDownOptions = new ArrayList<>();
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {
                    Select dropDown = new Select(element);
                    List<WebElement> selectedElements = dropDown.getOptions();
                    for (WebElement elem : selectedElements) {
                        dropDownOptions.add(elem.getText());
                    }
                    bFlag = true;

                    break;
                }
            }

            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL,
                        "Failed to fetch Dropdown option for dropdown " + elementName);

            }

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while finding the dropdown options from " + elementName, e);
        }

        return dropDownOptions;
    }

    public boolean doubleClick(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                WebElement ele = verifyElementClickable(element);
                if (ele != null) {
                    JavascriptExecutor jsExec = (JavascriptExecutor) getDriver();

                    try {
                        jsExec.executeScript("arguments[0].scrollIntoView({block: \"center\"});", ele);
                    } catch (Exception e) {

                    }
                    Actions action = new Actions(getDriver());
                    action.doubleClick(ele).build().perform();
                    bFlag = true;
                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Failed to perform double click on element " + elementName);
            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Timeout Exception occured while performing double click on element " + elementName, t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while performing double click on element " + elementName, e);
        }

        return bFlag;
    }

    public boolean rightClick(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                WebElement ele = verifyElementClickable(element);
                if (ele != null) {
                    JavascriptExecutor jsExec = (JavascriptExecutor) getDriver();

                    try {
                        jsExec.executeScript("arguments[0].scrollIntoView({block: \"center\"});", ele);
                    } catch (Exception e) {

                    }
                    Actions action = new Actions(getDriver());
                    action.contextClick(ele).build().perform();
                    waitForPageLoad();
                    bFlag = true;

                    break;
                }
            }
            if (bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Failed to Right click on element " + elementName);

            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Timeout Exception occured while performing right click on element " + elementName, t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while performing right click on element " + elementName, e);
        }

        return bFlag;
    }

    public boolean pressEnter(String elementName, String property) {
        boolean bFlag = false;

        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {
                    JavascriptExecutor jsExec = (JavascriptExecutor) getDriver();

                    try {
                        jsExec.executeScript("arguments[0].scrollIntoView({block: \"center\"});", element);
                    } catch (Exception e) {

                    }
                    element.sendKeys(Keys.ENTER);
                    waitForPageLoad();
                    bFlag = true;

                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Press enter failed on element " + elementName);
            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Timeout Exception occurred while performing press enter action on element " + elementName,
                    t);

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while performing press enter action on element " + elementName, e);
        }

        return bFlag;
    }

    public String getText(String elementName, String property) {
        boolean bFlag = false;
        String text = null;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {
                scrolltoCenterElement(element);
                text = element.getText();
                if (text != null) {
                    bFlag = true;

                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Unable to get text from element " + elementName);

            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception in fetching text from element " + elementName,
                    t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception in fetching text from element " + elementName, e);
        }

        return text;
    }

    public String getTextFromTextboxOrDropbox(String elementName, String property) {
        boolean bFlag = false;
        String text = null;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {
                    scrolltoCenterElement(element);
                    text = element.getAttribute("value");
                    bFlag = true;

                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Unable to get text from element " + elementName);
            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception in fetching text from element " + elementName,
                    t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception in fetching text from element " + elementName, e);
        }
        return text;
    }

    public String getValueOfAttribute(String elementName, String property, String attribute) {
        boolean bFlag = false;
        String text = null;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {
                    scrolltoCenterElement(element);
                    text = element.getAttribute(attribute);
                    bFlag = true;

                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL,
                        "Failed to get value of attribute '" + attribute + "' for element " + elementName);
            }

            return text;
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception while fetching value of attribute '"
                    + attribute + "' for element " + elementName, t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception while fetching value of attribute '" + attribute + "' for element " + elementName, e);
        }

        return null;
    }

    public void quitDriver() {
        try {
            getDriver().quit();
            Reporting.getLogger().log(LogStatus.PASS, "Successfuly quit driver");
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while quitting the driver ", e);
        }
    }

    public boolean isTextPresent(String elementName, String property, String text) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {
                    scrolltoCenterElement(element);
                    highlighElement(element);
                    String elemntText = element.getText();
                    if (elemntText.equals(text)) {
                        bFlag = true;
                    }

                    break;
                }
            }

            if (bFlag) {
                Reporting.getLogger().log(LogStatus.PASS, "Text '" + text + "' present in element" + elementName);
            } else {

                Reporting.getLogger().log(LogStatus.FAIL, "Text '" + text + "' not present in element" + elementName);
            }

            return bFlag;
        } catch (TimeoutException timeoutException) {

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception while verifying Text '" + text + "' present in element" + elementName, e);
        }

        return false;
    }

    private void scrolltoCenterElement(WebElement element) {
        JavascriptExecutor jsExec = (JavascriptExecutor) getDriver();

        try {
            jsExec.executeScript("arguments[0].scrollIntoView({block: \"center\"});", element);
        } catch (Exception e) {

        }
    }

    public List<WebElement> getWebElementsList(String elementName, String property) {
        try {
            List<WebElement> elements = new ArrayList<>();

            Map<String, List<String>> locatorsMapValues = getLocatorsMap(property);

            for (String locator : locatorsMapValues.keySet()) {

                List<String> locatorVal = locatorsMapValues.get(locator);
                for (String locValue : locatorVal) {
//                    WebElement element = getWebelement(locator, locValue);
//                    if (element != null) {
                    elements = getWebElementsOfElement(locator, locValue);
//                    }
                }
            }

            if (elements.isEmpty()) {

                Reporting.getLogger().log(LogStatus.INFO, "Failed to get web element list for locator '" + elementName);
            } else {

                Reporting.getLogger().log(LogStatus.PASS, "Web element list for locator " + elementName + " found.");
                return elements;
            }
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception while fetching Web element list for locator '" + elementName, e);

        }
        Reporting.getLogger().log(LogStatus.INFO, "Failed to get web element list for locator '" + elementName);

        throw new RuntimeException("Error while processing WebElement " + elementName);
    }

    public List<String> getTextFromWebElementList(String elementName, String property) {
        try {
            List<WebElement> elements = getWebElementsList(elementName, property);

            if (elements == null) {
                throw new RuntimeException("Unable to find the element " + elementName
                        + " whose property value is " + property);
            }
            List<String> elementText = new ArrayList<>();

            for (WebElement element : elements) {
                scrolltoCenterElement(element);
                highlighElement(element);
                elementText.add(element.getText());
            }
            if (elementText.isEmpty()) {

                Reporting.getLogger().log(LogStatus.FAIL, "No Text for Element " + elementName + " list.");
            } else {

                Reporting.getLogger().log(LogStatus.PASS, "Text for each element in element list is " + elementText);
            }

            return elementText;
        } catch (TimeoutException t) {
            return null;
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while fetching text from element list for locator " + elementName, e);
//            throw new RuntimeException("Exception occurred while fetching text from element list for locator " + elementName);
            return new ArrayList<>();
        }
    }

    public String getPageTitle() {
        try {
            String pageTitle = driver.getTitle();
            return pageTitle;
        } catch (TimeoutException timeoutException) {

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.FAIL, "Exception while fetching page title ");
        }

        return null;
    }

    public boolean isElementExists(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {
                    scrolltoCenterElement(element);
                    highlighElement(element);
                    if (element.isEnabled()) {

                        bFlag = true;

                        break;
                    }
                }
            }
            if (bFlag) {
                Reporting.getLogger().log(LogStatus.PASS, "Element '" + elementName + "' exists");
            } else {

                Reporting.getLogger().log(LogStatus.INFO, "Element '" + elementName + "' does not exists");
            }

            return bFlag;
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.INFO,
                    "Timeout Exception occured while checking the existance of element '" + elementName, t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.INFO,
                    "Exception occured while checking the existance of element '" + elementName, e);
        }

        return false;
    }

    public boolean isElementSelected(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {
                    scrolltoCenterElement(element);
                    highlighElement(element);
                    if (element.isSelected()) {

                        bFlag = true;

                        break;
                    }
                }
            }
            if (bFlag) {
                Reporting.getLogger().log(LogStatus.PASS, "Element '" + elementName + "' selected");
            } else {

                Reporting.getLogger().log(LogStatus.INFO, "Element '" + elementName + "' does not exists");
            }

            return bFlag;
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.INFO,
                    "Timeout Exception occurred while checking the existence of element '" + elementName, t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.INFO,
                    "Exception occurred while checking the existence of element '" + elementName, e);
        }

        return false;
    }

    public boolean switchAndAcceptAlert() {
        boolean bFlag = false;

        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), 5L);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());

            if (alert != null) {
                alert.accept();
                bFlag = true;
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Alert is not Accepted");
            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL, "Timeout Expetion occured while accepting Alert", t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Expetion occured while accepting Alert", e);
        }

        return bFlag;
    }

    public boolean switchAndCancelAlert() {
        boolean bFlag = false;

        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), 5L);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());

            if (alert != null) {
                alert.dismiss();
                bFlag = true;
            }

            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Alert is not Canceled");
            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception while canceling the Alert", t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception while canceling the Alert", e);
        }
        return bFlag;
    }

    public String switchAndGetTextFromAlert() {
        String text = null;

        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), 5L);
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());

            if (alert != null) {
                text = alert.getText();
                Reporting.getLogger().log(LogStatus.PASS, "Text '" + text + "' found in alert.");
            } else {

                Reporting.getLogger().log(LogStatus.FAIL, "Text not found in alert.");
            }

            return text;
        } catch (TimeoutException t) {
            return null;
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception while getting text from alert.");
            return null;
        }
    }

    public boolean waitUntilElementIsVisible(String elementName, String property, long timeOutInSeconds) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(elementName, property, timeOutInSeconds);
            if (elements == null) {
                return false;
            }
            for (WebElement element : elements) {
                highlighElement(element);
                Thread.sleep(300L);
                if (existenceofWebelement(element, timeOutInSeconds)) {

                    bFlag = true;

                    break;
                }
            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception while locating '" + elementName,
                    t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating '" + elementName, e);
        }
        return bFlag;
    }

    public boolean waitUntilElementIsVisible(String elementName, String property, long timeOutInSeconds, boolean toLog) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(elementName, property, timeOutInSeconds);
            if (elements == null) {
                return false;
            }
            for (WebElement element : elements) {
                highlighElement(element);
                Thread.sleep(300L);
                if (existenceofWebelement(element, timeOutInSeconds)) {

                    bFlag = true;

                    break;
                }
            }
        } catch (TimeoutException t) {
            if (toLog) {
                Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception while locating '" + elementName,
                        t);
            } else {
                Reporting.getLogger().log(LogStatus.INFO, "Error Timeout handled while locating '" + elementName,
                        t);
            }

        } catch (Exception e) {
            if (toLog) {
                if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
                ) {
                    throw new RuntimeException(e);
                }

                Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating '" + elementName, e);
            } else {
                Reporting.getLogger().log(LogStatus.INFO, "Error handled while locating element '" + elementName, e);

            }

        }
        return bFlag;
    }

    public boolean waitUntilElementIsInVisible(String elementName, String property) {
        boolean bFlag = false;
        try {
            if (verifyElementInvisibility(property, this.timeOutDuration.getSeconds())) {

                bFlag = true;
            } else {

                Reporting.getLogger().log(LogStatus.FAIL,
                        "Element '" + elementName + "' did not get invisible in specified time");
            }

        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception while locating Element " + elementName,
                    t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating Element " + elementName, e);
        }
        return bFlag;
    }

    public boolean waitUntilElementIsVisible(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(elementName, property, this.timeOutDuration.getSeconds());

            if(elements==null) {
                return false;
            }
            for (WebElement element : elements) {
                highlighElement(element);
                Thread.sleep(300L);
                if (existenceofWebelement(element, this.timeOutDuration.getSeconds())) {

                    bFlag = true;

                    break;
                }
            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception while locating '" + elementName,
                    t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating '" + elementName, e);
        }
        return bFlag;
    }

    public boolean waitUntilElementIsVisible(String elementName, String property, boolean toLog) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(elementName, property, this.timeOutDuration.getSeconds());
            if(elements==null) {
                return false;
            }
            for (WebElement element : elements) {
                highlighElement(element);
                Thread.sleep(300L);
                if (existenceofWebelement(element, this.timeOutDuration.getSeconds())) {

                    bFlag = true;

                    break;
                }
            }
        } catch (TimeoutException t) {
            if (toLog) {
                Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception while locating '" + elementName,
                        t);
            } else {
                Reporting.getLogger().log(LogStatus.INFO, "Handled Timeout Exception while locating '" + elementName,
                        t);
            }
        } catch (Exception e) {

            if (toLog) {
                Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating '" + elementName, e);
            } else {
                Reporting.getLogger().log(LogStatus.INFO, "Handled Exception while locating '" + elementName, e);
            }
        }
        return bFlag;
    }

    public boolean waitUntilElementIsInVisible(String elementName, String property, long timeOut, boolean toLog) {
        boolean bFlag = false;
        try {
            if (verifyElementInvisibility(property, timeOut)) {

                bFlag = true;
            } else {
                if (toLog)
                    Reporting.getLogger().log(LogStatus.FAIL,
                            "Element '" + elementName + "' did not get invisible in specified time");
            }

        } catch (TimeoutException t) {
            if (toLog) {
                Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception while locating Element " + elementName,
                        t);
            }
        } catch (Exception e) {
            if (toLog) {
                if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
                ) {
                    throw new RuntimeException(e);
                }
                Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating Element " + elementName, e);
            }
        }
        return bFlag;
    }

    public boolean waitUntilElementIsInVisible(String elementName, String property, long timeOut) {
        boolean bFlag = false;
        try {
            if (verifyElementInvisibility(property, timeOut)) {

                bFlag = true;
            } else {

                Reporting.getLogger().log(LogStatus.FAIL,
                        "Element '" + elementName + "' did not get invisible in specified time");
            }

        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception while locating Element " + elementName,
                    t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating Element " + elementName, e);
        }
        return bFlag;
    }


    public boolean waitUntilElementToBeClickable(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {
                highlighElement(element);
                WebElement ele = verifyElementClickable(element);
                if (ele != null) {
                    bFlag = true;

                    break;
                }
                if (bFlag) {
                    Reporting.getLogger().log(LogStatus.PASS, "Element '" + element + "' is clickable");
                    continue;
                }
                Reporting.getLogger().log(LogStatus.FAIL, "Element '" + elementName + "' was not clickable");
            }

        } catch (TimeoutException timeoutException) {

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Element '" + elementName + "' was not clickable");
        }
        return bFlag;
    }

    public boolean areElementsPresent(String elementName, String property) {
        if (getWebElementsList(elementName, property).size() != 0) {
            Reporting.getLogger().log(LogStatus.PASS, "Elements are present for the locator " + elementName);
            return true;
        }
        Reporting.getLogger().log(LogStatus.FAIL, "Elements are not present for the locator " + elementName);
        return false;
    }

    public String getCurrentWindowHandle() {
        String windowHandle = null;

        try {
            windowHandle = getDriver().getWindowHandle();
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while getting the current window handle",
                    t);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while getting the current window handle", e);
        }
        return windowHandle;
    }

    public Set<String> getAllWindowHandles() {
        try {
            Set<String> windowHandles = null;

            windowHandles = getDriver().getWindowHandles();
            return windowHandles;
        } catch (TimeoutException t) {
            return null;
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while getting the all window handle", e);
            return null;
        }
    }

    public boolean switchToWindow(String previousWindowHandle) {
        boolean bFlag = false;

        try {
            WebDriver driver = getDriver().switchTo().window(previousWindowHandle);
            if (driver != null && getCurrentWindowHandle().equals(previousWindowHandle)) {
                bFlag = true;

                Reporting.getLogger().log(LogStatus.PASS,
                        "Switched to window with handle '" + previousWindowHandle + "'");
            } else {

                Reporting.getLogger().log(LogStatus.FAIL,
                        "Failed to switch to window with handle '" + previousWindowHandle + "'");
            }

        } catch (TimeoutException timeoutException) {

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception while switching to window with handle '" + previousWindowHandle + "'", e);
        }

        return bFlag;
    }

    public void switchToNewWindow(int i) {
        try {
            Set<String> windows = getAllWindowHandles();
            getDriver().switchTo().window(windows.toArray(new String[windows.size()])[i]);
            Reporting.getLogger().log(LogStatus.PASS, "Switched to new window");
        } catch (TimeoutException timeoutException) {

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while switching to new window ", e);
        }
    }

    public void closeWindow(int i) {
        try {
            Set<String> windows = getDriver().getWindowHandles();
            getDriver().switchTo().window(windows.toArray(new String[windows.size()])[i]).close();
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while closing the window ", e);
        }
    }

    public void refreshPage() {
        try {
            getDriver().navigate().refresh();
        } catch (TimeoutException timeoutException) {

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while refreshing the page", e);
        }
    }

    public void navigateToPreviousPage() {
        try {
            getDriver().navigate().back();
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while navigating to previous page", e);
        }
    }

    public void mouseHover(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {

                    highlighElement(element);
                    Actions action = new Actions(getDriver());
                    action.moveToElement(element).build().perform();
                    bFlag = true;

                    break;
                }
            }
            if (bFlag) {
                Reporting.getLogger().log(LogStatus.PASS, "Mouse hover performed on element '" + elementName);
            } else {

                Reporting.getLogger().log(LogStatus.FAIL, "Failed to perform mouse hover on element '" + elementName);
            }

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while performing mouse hover operation over element '" + elementName, e);
        }
    }

    public boolean mouseHoverClickChild(String parentproperty, String childproperty) {
        boolean bFlag = false;

        try {
            List<WebElement> parentElements = getWebElementList(parentproperty, null);
            List<WebElement> childElements = getWebElementList(childproperty, null);

            for (WebElement element : parentElements) {

                if (visibilityofWebelement(element)) {
                    for (WebElement childElement : childElements) {
                        if (visibilityofWebelement(childElement)) {
                            Actions action = new Actions(getDriver());
                            action.moveToElement(element).moveToElement(childElement).click().build().perform();
                            bFlag = true;
                        }

                    }
                }
            }
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while clicking child element after mouse hover", e);
        }

        return bFlag;
    }

    public String getPageSource() {
        String pageSource = null;

        try {
            pageSource = getDriver().getPageSource();
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while finding the page source", e);
        }

        return pageSource;
    }

    public void deleteAllCookies() {
        try {
            getDriver().manage().deleteAllCookies();
            Thread.sleep(3500L);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while deleting the cookies", e);
        }
    }

    public String getCurrentUrl() {
        try {
            String currenturl = getDriver().getCurrentUrl();
            return currenturl;
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while getting the current URL", e);
            return null;
        }
    }

    public void switchToIframe() {
        try {
            (new WebDriverWait(getDriver(), 15L))
                    .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.tagName("iframe")));
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while switching to iframe", e);
        }
    }

    public void switchToIframeById(String iFrameId) {
        try {
            (new WebDriverWait(getDriver(), 15L))
                    .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id(iFrameId)));
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while switching to iframe with id" + iFrameId,
                    e);
        }
    }

    public void switchToIframeByIndex(int frameIndex) {
        try {
            (new WebDriverWait(getDriver(), 15L))
                    .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.tagName("iframe")));
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while switching to iframe", e);
        }
    }

    public boolean switchToIframe(String elementName, String property) {
        boolean bFlag = false;

        try {
            List<WebElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {
                    getDriver().switchTo().frame(element);
                    bFlag = true;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Failed to switch to iframe " + elementName);

            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Timeout Exception occured while switching to iframe " + elementName, t);

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while switching to iframe " + elementName, e);
        }
        return bFlag;
    }

    public void deselectIframe() {
        try {
            getDriver().switchTo().defaultContent();
            Reporting.getLogger().log(LogStatus.PASS, "Deselect the ifame");
        } catch (TimeoutException timeoutException) {

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while deselecting the iframe", e);
        }
    }

    public void waitFor(int enterMiliSeconds) {
        try {
            Thread.sleep(enterMiliSeconds);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

    public void waitForPageLoad() {
        try {

            getDriver().manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            waitForJSandJQueryToLoad();

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


//            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while waiting for page load", e);
        }
    }

    public void closeCurrentWindow() {
        try {
            getDriver().close();
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while closing current window", e);
        }
    }

    public boolean verifyTitle(String title) {
        if (getDriver().getTitle().equalsIgnoreCase(title)) {
            Reporting.getLogger().log(LogStatus.PASS, "Expected title " + title + " is equal to current title: " + getDriver().getTitle());
            return true;
        } else {
            Reporting.getLogger().log(LogStatus.FAIL, "Expected title " + title + " is equal to current title: " + getDriver().getTitle());
            return false;

        }
    }

    public String getCssValue(String elementName, String property, String cssAttribute) {
        boolean bFlag = false;
        String value = null;
        try {
            List<WebElement> elements = getWebElementList(property, null);

            for (WebElement element : elements) {

                if (visibilityofWebelement(element)) {
                    value = element.getCssValue(cssAttribute);
                    bFlag = true;

                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Unable to get Css attribute for element '" + elementName);
            }
        } catch (TimeoutException timeoutException) {

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception in fetching text from element " + elementName, e);
        }

        return value;
    }

    public String getExceptionDetails(Exception e) {
        return e.getLocalizedMessage();
    }

    public String setPageLoad(String pageName) {
        try {
            final JavascriptExecutor js = (JavascriptExecutor) driver;
            String loadTime = (String) js.executeScript(
                    "var t = performance.timing; var time; var start = t.redirectStart == 0 ? t.fetchStart : t.redirectStart;"
                            + " if (t.loadEventEnd > 0) { time = String(((t.loadEventEnd - start) / 1000).toPrecision(3)).substring(0, 4);}return  time;");
            if (loadTime != null)

                Reporting.getLogger().logPageLoadTime(LogStatus.PASS, "Page load time for page:- " + pageName,
                        loadTime);
            else {

                Reporting.getLogger().logPageLoadTime(LogStatus.FAIL,
                        "Unable to get page load time for page:- " + pageName, loadTime);
            }

            return loadTime;
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while fetching pag load time for page " + pageName, e);
        }

        return null;
    }

    private WebElement getWebelement(String locator, String locatorValue) {
        try {
            Duration pollingDuration;
            Duration timeOutDuration;
            if (isPageLoaded()) {
                pollingDuration = Duration.of(250, ChronoUnit.MILLIS);
                timeOutDuration = Duration.of(1, ChronoUnit.SECONDS);
            } else {
                pollingDuration = this.pollingDuration;
                timeOutDuration = this.timeOutDuration;
            }

//            FluentWait<WebDriver> driverWait = new FluentWait<WebDriver>(getDriver());
            WebDriverWait driverWait = new WebDriverWait(getDriver(), 15);
//            driverWait.pollingEvery(pollingDuration);
//            driverWait.withTimeout(timeOutDuration);
            driverWait.ignoring(java.util.NoSuchElementException.class);
            driverWait.ignoring(StaleElementReferenceException.class);
            driverWait.ignoring(ElementNotVisibleException.class);

            return findElement(locator, locatorValue, driverWait);
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            return null;
        }
    }

    private WebElement getWebelement(String locator, String locatorValue, long timeOutInSeconds) {
        Duration pollingDuration;
//        if (isPageLoaded()) {
//            pollingDuration = Duration.of(5, ChronoUnit.MILLIS);
////            timeOutInSeconds = 1;
//        } else {
//            pollingDuration = this.pollingDuration;
//        }
//        FluentWait<WebDriver> driverWait = new FluentWait<WebDriver>(getDriver());

        if (timeOutInSeconds > 100) {
            timeOutInSeconds = 15;
        }
        WebDriverWait driverWait = new WebDriverWait(getDriver(), timeOutInSeconds);
//        driverWait.pollingEvery(pollingDuration);
//        Duration timeout = Duration.of(timeOutInSeconds, ChronoUnit.SECONDS);
//        driverWait.withTimeout(timeout);
        driverWait.ignoring(java.util.NoSuchElementException.class);
        driverWait.ignoring(StaleElementReferenceException.class);
        driverWait.ignoring(ElementNotVisibleException.class);

        return findElement(locator, locatorValue, driverWait);
    }

    @SuppressWarnings("unchecked")
    private WebElement findElement(String locator, String locatorValue, FluentWait<WebDriver> driverWait) {
        WebElement element = null;
        final String locVal = locatorValue;

        try {
            Stopwatch stopwatch = null;
            if (locator.equalsIgnoreCase("id")) {
                if (this.locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            return WebLibrary.this.getDriver().findElement(By.id(locVal));
//                        }
//                    };

                    element = driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(locVal)));
                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "id");
                        this.jsonObj.put("isWorking", "Yes");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
                    }
                } catch (Exception e) {

                    if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
                    ) {
                        throw new RuntimeException(e);
                    }

                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "id");
                        this.jsonObj.put("isWorking", "No");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
                    }
                    throw e;
                }
            }
            if (locator.equalsIgnoreCase("xpath")) {
                if (this.locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            return WebLibrary.this.getDriver().findElement(By.xpath(locVal));
//                        }
//                    };
//
//                    element = driverWait.until(waitForElement);
                    element = driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locVal)));

                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "xpath");
                        this.jsonObj.put("isWorking", "Yes");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
                    }
                } catch (Exception e) {

                    if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
                    ) {
                        throw new RuntimeException(e);
                    }


                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "xpath");
                        this.jsonObj.put("isWorking", "No");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
                    }
                    throw e;
                }
            }
            if (locator.equalsIgnoreCase("className")) {
                if (this.locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            return WebLibrary.this.getDriver().findElement(By.className(locVal));
//                        }
//                    };
//
//                    element = driverWait.until(waitForElement);
                    element = driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className(locVal)));

                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "className");
                        this.jsonObj.put("isWorking", "Yes");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
                    }
                } catch (Exception e) {

                    if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
                    ) {
                        throw new RuntimeException(e);
                    }

                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "className");
                        this.jsonObj.put("isWorking", "No");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
                    }
                    throw e;
                }
            }
            if (locator.equalsIgnoreCase("name")) {
                if (this.locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            return WebLibrary.this.getDriver().findElement(By.name(locVal));
//                        }
//                    };
//
//                    element = driverWait.until(waitForElement);

                    element = driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name(locVal)));

                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "name");
                        this.jsonObj.put("isWorking", "Yes");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
                    }
                } catch (Exception e) {

                    if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
                    ) {
                        throw new RuntimeException(e);
                    }

                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "name");
                        this.jsonObj.put("isWorking", "No");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
                    }
                    throw e;
                }
            }
            if (locator.equalsIgnoreCase("tagName")) {
                if (this.locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            return WebLibrary.this.getDriver().findElement(By.tagName(locVal));
//                        }
//                    };
//
//                    element = driverWait.until(waitForElement);

                    element = driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName(locVal)));

                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "tagName");
                        this.jsonObj.put("isWorking", "Yes");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
                    }
                } catch (Exception e) {

                    if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
                    ) {
                        throw new RuntimeException(e);
                    }

                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "tagName");
                        this.jsonObj.put("isWorking", "No");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
                    }
                    throw e;
                }
            }
            if (locator.equalsIgnoreCase("css")) {
                if (this.locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            return WebLibrary.this.getDriver().findElement(By.cssSelector(locVal));
//                        }
//                    };
//
//                    element = driverWait.until(waitForElement);
                    element = driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(locVal)));

                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "css");
                        this.jsonObj.put("isWorking", "Yes");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
                    }
                } catch (Exception e) {

                    if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
                    ) {
                        throw new RuntimeException(e);
                    }

                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "css");
                        this.jsonObj.put("isWorking", "No");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
                    }
                    throw e;
                }
            }

            if (locator.equalsIgnoreCase("linkText")) {
                if (this.locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            return WebLibrary.this.getDriver().findElement(By.linkText(locVal));
//                        }
//                    };
//
//                    element = driverWait.until(waitForElement);
                    element = driverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(locVal)));

                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "linkText");
                        this.jsonObj.put("isWorking", "Yes");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
                    }
                } catch (Exception e) {

                    if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
                    ) {
                        throw new RuntimeException(e);
                    }

                    if (this.locatorTimeFlag) {
                        stopwatch.stop();
                        this.jsonObj.put("Locator", "linkText");
                        this.jsonObj.put("isWorking", "No");
                        this.jsonObj.put("time", Long.valueOf(stopwatch.elapsed(TimeUnit.MICROSECONDS)));
                    }
                    throw e;
                }
            }
            this.jsonObj.put("value", locVal);
            return element;
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            this.jsonObj.put("value", locVal);
            Reporting.getLogger().log(LogStatus.INFO, " Locator " + locator + ":" + locatorValue + " not found", e);
            throw e;
        }
    }

    private boolean visibilityofWebelement(WebElement element) {
        try {

            Duration pollingDuration;
            Duration timeOutDuration;
            if (isPageLoaded()) {
                pollingDuration = Duration.of(5, ChronoUnit.MILLIS);
                timeOutDuration = Duration.of(1, ChronoUnit.MILLIS);
            } else {
                pollingDuration = this.pollingDuration;
                timeOutDuration = this.timeOutDuration;
            }

            FluentWait<WebElement> _waitForElement = new FluentWait<WebElement>(element);
            _waitForElement.pollingEvery(pollingDuration);
            _waitForElement.withTimeout(timeOutDuration);
            _waitForElement.ignoring(java.util.NoSuchElementException.class);
            _waitForElement.ignoring(StaleElementReferenceException.class);
            _waitForElement.ignoring(ElementNotVisibleException.class);

            Function<WebElement, Boolean> elementVisibility = new Function<WebElement, Boolean>() {

                public Boolean apply(WebElement element) {
                    return Boolean.valueOf(element.isDisplayed());
                }
            };

            return _waitForElement.until(elementVisibility).booleanValue();
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occurred while verifying the visibility of an element ",
                    e);

            return false;
        }
    }

    private boolean visibilityofWebelement(WebElement element, long timeOutInSeconds) {
        try {
            FluentWait<WebElement> _waitForElement = new FluentWait<WebElement>(element);
            _waitForElement.pollingEvery(this.pollingDuration);
            Duration timeout = Duration.of(timeOutInSeconds, ChronoUnit.SECONDS);
            _waitForElement.withTimeout(timeout);
            _waitForElement.ignoring(java.util.NoSuchElementException.class);
            _waitForElement.ignoring(StaleElementReferenceException.class);
            _waitForElement.ignoring(ElementNotVisibleException.class);

            Function<WebElement, Boolean> elementVisibility = new Function<WebElement, Boolean>() {

                public Boolean apply(WebElement element) {
                    return Boolean.valueOf(element.isDisplayed());
                }
            };

            return _waitForElement.until(elementVisibility).booleanValue();
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while verifying the visibility of an element ",
                    e);

            return false;
        }
    }

    private boolean existenceofWebelement(WebElement element, long timeOutInSeconds) {
        try {
//            FluentWait<WebElement> _waitForElement = new FluentWait<WebElement>(element);
//            WebDriverWait _waitForElement = new WebDriverWait(driver,15);
////            _waitForElement.pollingEvery(this.pollingDuration);
////            Duration timeout = Duration.of(timeOutInSeconds, ChronoUnit.SECONDS);
////            _waitForElement.withTimeout(timeout);
//            _waitForElement.ignoring(java.util.NoSuchElementException.class);
//            _waitForElement.ignoring(StaleElementReferenceException.class);
//            _waitForElement.ignoring(ElementNotVisibleException.class);
//
//            Function<WebElement, Boolean> elementVisibility = new Function<WebElement, Boolean>() {
//
//                public Boolean apply(WebElement element) {
//                    return Boolean.valueOf(element.isDisplayed());
//                }
//            };

            WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSeconds);
            wait.ignoring(java.util.NoSuchElementException.class);
            wait.ignoring(StaleElementReferenceException.class);
            wait.ignoring(ElementNotVisibleException.class);


            return wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
        } catch (Exception exception) {

            return false;
        }
    }

    private WebElement verifyElementClickable(WebElement element) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), 15L);
            return wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            throw e;
        }
    }

    private boolean verifyElementInvisibility(String properties, long timeOutInSeconds) {
        boolean elem = false;

        try {
            Map<String, String> locatorsMap = new HashMap<>();

            String[] locatorValues = properties.split("\\|");

            for (String locator : locatorValues) {
                locatorsMap.put(locator.split("~")[0].trim(), locator.split("~")[1].trim());
            }

            for (String locator : locatorsMap.keySet()) {
                WebDriverWait wait = new WebDriverWait(getDriver(), timeOutInSeconds);

                if (locator.equalsIgnoreCase("xpath")) {

                    elem = wait
                            .until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(locatorsMap.get(locator))))
                            .booleanValue();
                    continue;
                }
                if (locator.equalsIgnoreCase("classname")) {
                    elem = wait.until(
                            ExpectedConditions.invisibilityOfElementLocated(By.className(locatorsMap.get(locator))))
                            .booleanValue();
                    continue;
                }
                if (locator.equalsIgnoreCase("name")) {

                    elem = wait
                            .until(ExpectedConditions.invisibilityOfElementLocated(By.name(locatorsMap.get(locator))))
                            .booleanValue();
                    continue;
                }
                if (locator.equalsIgnoreCase("id")) {
                    elem = wait
                            .until(ExpectedConditions.invisibilityOfElementLocated(By.id(locatorsMap.get(locator))))
                            .booleanValue();
                    continue;
                }
                if (locator.equalsIgnoreCase("css")) {
                    elem = wait.until(
                            ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(locatorsMap.get(locator))))
                            .booleanValue();
                    continue;
                }
                if (locator.equalsIgnoreCase("tagname")) {
                    elem = wait.until(
                            ExpectedConditions.invisibilityOfElementLocated(By.tagName(locatorsMap.get(locator))))
                            .booleanValue();
                    continue;
                }
                if (locator.equalsIgnoreCase("linkText")) {
                    elem = wait.until(
                            ExpectedConditions.invisibilityOfElementLocated(By.linkText(locatorsMap.get(locator))))
                            .booleanValue();
                }
            }

        } catch (Exception exception) {
        }

        return elem;
    }

    private void checkAvailableLocators(Map<String, List<String>> locatorsMapValues, List<WebElement> elements,
                                        String elementName) {
        Set<String> expectedLocators = new HashSet<>(
                Arrays.asList("id", "xpath", "linkText", "css", "name", "class"));
        Map<String, String> possibleLocators = new HashMap<>();
        try {
            expectedLocators.removeAll(locatorsMapValues.keySet());
            WebElement fetchedElement = null;
            for (WebElement element : elements) {
                if (visibilityofWebelement(element) && element != null) {
                    fetchedElement = element;

                    break;
                }
            }

            if (fetchedElement != null) {
                for (String locator : expectedLocators) {
                    if (locator.equalsIgnoreCase("id")) {
                        String id = fetchedElement.getAttribute("id");
                        if (!(id == null || id.isEmpty())) {
                            possibleLocators.put("id", id);
                        }
                    }
                    if (locator.equalsIgnoreCase("class")) {
                        String className = fetchedElement.getAttribute("class");
                        if (!(className == null || className.isEmpty())) {
                            possibleLocators.put("className", className);
                        }
                    }
                    if (locator.equalsIgnoreCase("linkText")) {

                        String tag = fetchedElement.getTagName();
                        if (tag.equalsIgnoreCase("a")) {
                            String text = fetchedElement.getText();
                            if (text != null) {
                                possibleLocators.put("linkText", text);
                            }
                        }
                        continue;
                    }
                    if (locator.equalsIgnoreCase("css")) {
                        String tag = fetchedElement.getTagName();
                        JavascriptExecutor executor = (JavascriptExecutor) driver;
                        Object aa = executor.executeScript(
                                "var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;",
                                fetchedElement);

                        String[] attributesValue = String.valueOf(aa).split(",");
                        String cssList = "";
                        for (String att : attributesValue) {
                            String[] value = att.split("=");
                            if (value.length == 2) {

                                String css = tag + "[" + value[0].replace("{", "").trim() + "="
                                        + value[1].replace("}", "").trim() + "]";
                                if (cssList.isEmpty()) {
                                    cssList = css;
                                } else {
                                    cssList = cssList + ";" + css;
                                }
                            }
                        }
                        possibleLocators.put("css", cssList);
                        continue;
                    }
                    if (locator.equalsIgnoreCase("name")) {
                        String name = fetchedElement.getAttribute("name");
                        if (!(name == null || name.isEmpty())) {
                            possibleLocators.put("name", name);
                        }
                    }
                    if (locator.equalsIgnoreCase("xpath")) {
                        String tag = fetchedElement.getTagName();
                        String text = fetchedElement.getText();
                        String xpath = "";
                        if (!text.isEmpty()) {
                            xpath = "//" + tag + "[contains(text(),'" + text + "')]";
                            possibleLocators.put("xpath", xpath);
                        }
                    }
                }
            }
            if (possibleLocators.size() > 0) {
                Reporting.getLogger().log(possibleLocators, elementName);
            }
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private List<WebElement> getWebElementList(String property, String elementName) {
        errorMsg = null;
        boolean bFlag = false;
        boolean locatorFlag = false;

        this.jsonArray = new JSONArray();
        Map<String, List<String>> locatorsMapValues = getLocatorsMap(property);
        List<WebElement> webElements = new ArrayList<>();

        try {
            for (String locator : locatorsMapValues.keySet()) {

                if (!this.locatorTimeFlag && bFlag) {
                    break;
                }
                List<String> locatorVal = locatorsMapValues.get(locator);
                for (String locValue : locatorVal) {
                    this.jsonObj = new JSONObject();
                    errorMsg += " Locator Type : " + locator + " and Locator Value : " + locValue;
                    WebElement element = getWebelement(locator, locValue);
                    if (element != null) {
                        webElements.add(element);
                        bFlag = true;
                        if (this.locatorTimeFlag) {
                            if (!locatorFlag) {
                                this.jsonObj.put("isUsed", "Yes");
                                locatorFlag = true;
                            } else {
                                this.jsonObj.put("isUsed", "No");
                            }

                        }
                    } else if (this.locatorTimeFlag) {
                        this.jsonObj.put("isUsed", "No");
                    }

                    this.jsonArray.add(this.jsonObj);
                }
                errorMsg += " not found ";
            }

            if (this.locatorTimeFlag) {
                Reporting.getLogger().log(this.jsonArray.toString(), elementName);
            }
            if (this.locatorSuggestionFlag)
                checkAvailableLocators(locatorsMapValues, webElements, elementName);
            return webElements;
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private List<WebElement> getWebElementList(String elementName, String properties, long timeOutInSeconds) {
        this.jsonArray = new JSONArray();
        boolean locatorFlag = false;

        try {
            Map<String, List<String>> locatorsMapValues = getLocatorsMap(properties);

            List<WebElement> webElements = new ArrayList<>();
            for (String locator : locatorsMapValues.keySet()) {

                List<String> locatorVal = locatorsMapValues.get(locator);
                for (String locValue : locatorVal) {
                    this.jsonObj = new JSONObject();
                    errorMsg += "Locator Type : " + locator + " and locator Value : " + locValue;
                    WebElement element = getWebelement(locator, locValue, timeOutInSeconds);
                    if (element != null) {
                        webElements.add(element);
                        if (this.locatorTimeFlag) {
                            if (!locatorFlag) {
                                this.jsonObj.put("isUsed", "Yes");
                                locatorFlag = true;
                            } else {
                                this.jsonObj.put("isUsed", "No");
                            }

                        }
                    } else if (this.locatorTimeFlag) {
                        this.jsonObj.put("isUsed", "No");
                    }

                    this.jsonArray.add(this.jsonObj);
                }
                errorMsg += " not found ";
            }

            if (this.locatorTimeFlag) {
                Reporting.getLogger().log(this.jsonArray.toString(), elementName);
            }
            return webElements;
        } catch (TimeoutException t) {
            return null;
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            throw e;
        }
    }

    private Map<String, List<String>> getLocatorsMap(String properties) {
        Map<String, List<String>> locatorsMapValues = new HashMap<>();

        try {
            String[] locatorValues = properties.split("\\|");

            for (String locator : locatorValues) {
                String locatorName = locator.split("~")[0].trim();
                String locatorValue = locator.split("~")[1].trim();
                if (locatorsMapValues.containsKey(locatorName)) {
                    List<String> locatorNameProperties = locatorsMapValues.get(locatorName);
                    locatorNameProperties.add(locatorValue);
                } else {
                    List<String> locatorVal = new ArrayList<>();
                    locatorVal.add(locatorValue);
                    locatorsMapValues.put(locatorName, locatorVal);
                }

            }

        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while getting locators from properties", e);
        }

        return locatorsMapValues;
    }

    public void highlighElement(WebElement element) {
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        js.executeScript("arguments[0].setAttribute('style', 'background: red; border: 2px solid red;');",
//                new Object[]{element});
    }

    private List<WebElement> getWebElementsOfElement(String locator, String locatorValue) {

        List<WebElement> elements = null;
        final String locVal = locatorValue;
//        FluentWait<WebDriver> driverWait = new FluentWait<>(getDriver());
        WebDriverWait driverWait = new WebDriverWait(getDriver(), 15);
//        driverWait.pollingEvery(pollingDuration);
//        driverWait.withTimeout(timeOutDuration);
        driverWait.ignoring(NoSuchElementException.class);
        driverWait.ignoring(StaleElementReferenceException.class);

        try {
            if (locator.equalsIgnoreCase("id")) {

//                Function<WebDriver, List<WebElement>> waitForElement = new Function<WebDriver, List<WebElement>>() {
//                    public List<WebElement> apply(WebDriver driverWait) {
//                        return WebLibrary.this.getDriver().findElements(By.id(locVal));
//                    }
//                };

//                elements = driverWait.until(waitForElement);
                elements = driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy((By.id(locVal))));

            }
            if (locator.equalsIgnoreCase("xpath")) {

//                Function<WebDriver, List<WebElement>> waitForElement = new Function<WebDriver, List<WebElement>>() {
//                    public List<WebElement> apply(WebDriver driverWait) {
//                        return WebLibrary.this.getDriver().findElements(By.xpath(locVal));
//                    }
//                };
//
//                elements = driverWait.until(waitForElement);
                elements = driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy((By.xpath(locVal))));

            }

            if (locator.equalsIgnoreCase("className")) {

//                Function<WebDriver, List<WebElement>> waitForElement = new Function<WebDriver, List<WebElement>>() {
//                    public List<WebElement> apply(WebDriver driverWait) {
//                        return WebLibrary.this.getDriver().findElements(By.className(locVal));
//                    }
//                };
//
//                elements = driverWait.until(waitForElement);
                elements = driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy((By.className(locVal))));

            }

            if (locator.equalsIgnoreCase("name")) {
//                Function<WebDriver, List<WebElement>> waitForElement = new Function<WebDriver, List<WebElement>>() {
//                    public List<WebElement> apply(WebDriver driverWait) {
//                        return WebLibrary.this.getDriver().findElements(By.name(locVal));
//                    }
//                };
//
//                elements = driverWait.until(waitForElement);
                elements = driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.name(locVal)));

            }
            if (locator.equalsIgnoreCase("tagName")) {

//                Function<WebDriver, List<WebElement>> waitForElement = new Function<WebDriver, List<WebElement>>() {
//                    public List<WebElement> apply(WebDriver driverWait) {
//                        return WebLibrary.this.getDriver().findElements(By.tagName(locVal));
//                    }
//                };
//
//                elements = driverWait.until(waitForElement);
                elements = driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy((By.tagName(locVal))));

            }

            if (locator.equalsIgnoreCase("css")) {

//                Function<WebDriver, List<WebElement>> waitForElement = new Function<WebDriver, List<WebElement>>() {
//                    public List<WebElement> apply(WebDriver driverWait) {
//                        return WebLibrary.this.getDriver().findElements(By.cssSelector(locVal));
//                    }
//                };
//
//                elements = driverWait.until(waitForElement);
                elements = driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy((By.cssSelector(locVal))));

            }

            if (locator.equalsIgnoreCase("linkText")) {

//                Function<WebDriver, List<WebElement>> waitForElement = new Function<WebDriver, List<WebElement>>() {
//                    public List<WebElement> apply(WebDriver driverWait) {
//                        return WebLibrary.this.getDriver().findElements(By.linkText(locVal));
//                    }
//                };
//
//                elements = driverWait.until(waitForElement);
                elements = driverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy((By.linkText(locVal))));

            }

            return elements;
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }


            Reporting.getLogger().log(LogStatus.INFO, " Locator " + locator + ":" + locatorValue + " not found :");

            return null;
        }
    }

    private boolean waitForJSandJQueryToLoad() {

        WebDriverWait wait = new WebDriverWait(getDriver(), 50);

        // wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return ((Long) ((JavascriptExecutor) getDriver()).executeScript("return jQuery.active") == 0);
                } catch (Exception e) {

                    if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
                    ) {
                        throw new RuntimeException(e);
                    }

                    // no jQuery present
                    return true;
                }
            }
        };

        // wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) getDriver()).executeScript("return document.readyState")
                        .toString().equals("complete");
            }
        };

        return wait.until(jQueryLoad) && wait.until(jsLoad);
    }

    public void scrollToElement(String property, String elementName) {
        boolean bFlag = false;
        List<WebElement> elements = getWebElementList(elementName, property);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            for (WebElement element : elements) {
                if (element != null) {
                    js.executeScript("arguments[0].scrollIntoView({block: \"center\"});", element);
                    bFlag = true;

                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Failed to scroll on Element '" + elementName + "'.");
            } else {
                Reporting.getLogger().log(LogStatus.PASS, "Scroll on Element '" + elementName + "'.");
            }
        } catch (Exception e) {

            if (e.getMessage() != null && e.getMessage().equalsIgnoreCase("TEST FAILED")
            ) {
                throw new RuntimeException(e);
            }

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while performing scroll action to element '" + elementName + "'", e);
            e.printStackTrace();
        }


    }


    private boolean isPageLoaded() {
        return isJqueryLoaded()
                && isJavaScriptLoaded();

    }

    private boolean isJqueryLoaded() {
        return ((JavascriptExecutor) getDriver()).executeScript("return document.readyState").equals("complete");
    }

    public boolean isJavaScriptLoaded() {
        return ((JavascriptExecutor) getDriver()).executeScript("return document.readyState")
                .toString().equals("complete");
    }

    public void updatePageTestData(String key, Object objectValue) {
        if (this.pageTestData == null) {
            pageTestData = new HashMap<>();
        }
        this.pageTestData.put(key, objectValue);
    }

    public Object getPageTestData(String key) {
        if (this.pageTestData == null) {

            throw new RuntimeException("Test data for page not setup");
        }
        if (!this.pageTestData.containsKey(key)) {
            Reporting.getLogger().log(LogStatus.FAIL, "Key doesn't exists " + key + "  Please verify your key");
            throw new RuntimeException("Key doesn't exists , please verify your key");
        }
        return this.pageTestData.get(key);
    }


    public boolean switchContext(String context) {
        return false;
    }

    public boolean tap(String elementName, String property) {
        return false;
    }

    public void tap(int xAxis, int yAxis) {
    }

    public boolean doubleTap(String elementName, String property) {
        return false;
    }

    public void doubleTap(int xAxis, int yAxis) {
    }

    public boolean scroll(String fromElement, String toElement) {
        return false;
    }

    public void scroll(int fromXCordinate, int fromYCordinate, int toXCordinate, int toYCordinate) {
    }

    public boolean zoom(String elementName, String property) {
        return false;
    }

    public boolean performMultiTouch(List<TouchAction> actionList) {
        return false;
    }

//    @Override
//    public SeeTestClient getSeeTestClient() {
//        return null;
//    }
//
//    @Override
//    public void setSeeTestClient(SeeTestClient seetestClient) {
//
//    }
}
