package com.xassure.framework.driver;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.xassure.reporting.logger.LogStatus;
import com.xassure.reporting.logger.Reporting;
import com.xassure.reporting.utilities.PropertiesFileHandler;
import io.appium.java_client.*;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("rawtypes")
public class MobileControlsLibrary implements MobileControls {
    private static String errorMsg = null;
    PageInfo pageInfo;
    Map<String, Object> pageTestData;
    AppiumDriver<MobileElement> driver;
    JSONObject jsonObj;
    JSONArray jsonArray;
    Duration pollingDuration = Duration.of(250, ChronoUnit.MILLIS);
    Duration timeOutDuration = Duration.of(2, ChronoUnit.SECONDS);
//    SeeTestClient seeTestClient;
    private boolean locatorTimeFlag = Boolean
            .parseBoolean(new PropertiesFileHandler().readProperty("setupConfig", "captureLocatorTime"));

    @Inject
    public MobileControlsLibrary(AppiumDriver<MobileElement> driver) {
        this.driver = driver;
        Reporting.setDriver(driver);
    }

    @Override
    public PageInfo getPageDetails() {
        return pageInfo;
    }

    @Override
    public void setPageDetails(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Override
    public AppiumDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(String... params) {
    }

    @Override
    public void launchApplication(String url) {
        driver.get(url);
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl.toLowerCase().contains(url.toLowerCase())) {
            Reporting.getLogger().log(LogStatus.PASS, "URL '" + url + "' is successfuly launched");
        } else if (currentUrl.isEmpty() && url.isEmpty() == false) {
            Reporting.getLogger().log(LogStatus.PASS, "Failed to launch URL '" + url + "'");
        }

    }

    @Override
    public boolean click(String elementName, String property) {
        boolean bFlag = false;
        List<MobileElement> elements = getWebElementList(property, elementName);
        try {
            for (MobileElement element : elements) {
                MobileElement ele = verifyElementClickable(element);
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
            e.printStackTrace();
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while clicking on element '" + elementName + "'", e);
        }
        return bFlag;
    }

    @Override
    public boolean clickUsingActionBuilder(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {

                MobileElement ele = verifyElementClickable(element);
                if (ele != null) {
                    // scrolltoCenterElement(element);

                    highlighElement(ele);
//                    Actions action = new Actions(getDriver());
//                    action.click(ele).build().perform();
                    TouchActions action = new TouchActions(driver);
                    action.click(element);
                    action.perform();
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
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while clicking on element '" + elementName + "' using action builder", e);
        }

        return bFlag;
    }

    @Override
    public boolean clickUsingJavaScriptExecutor(String elementName, String property) {
        // TODO Auto-generated method stub
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);
            JavascriptExecutor jsExec = (JavascriptExecutor) getDriver();
            for (MobileElement element : elements) {
                MobileElement ele = verifyElementClickable(element);
                if (ele != null) {
                    highlighElement(ele);
                    // scrolltoCenterElement(element);
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
            Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception occured while clicking on element '"
                    + elementName + "' using javascript executor", e);
        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while clicking on element '" + elementName + "' using javascript executor", e);
        }

        return bFlag;
    }

    @Override
    public void enterText(String elementName, String property, String textToEnter) {
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);
            for (MobileElement element : elements) {

                if (visibilityofWebelement(element)) {
//                    // scrolltoCenterElement(element);
                    element.clear();
                    highlighElement(element);
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
                    "Timeout Exception occured while entering text : '" + textToEnter + "' into '" + elementName);

        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while entering text : '" + textToEnter + "' into '" + elementName);
        }

    }

    public void enterText(String elementName, String property, Keys keys) {
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);
            for (MobileElement element : elements) {

                if (visibilityofWebelement(element)) {
                    element.clear();
                    highlighElement(element);
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
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while entering keys : '" + keys.name() + "' into '" + elementName, e);
        }
    }

    @Override
    public void enterTextUsingJavascriptExecutor(String elementName, String property, String textToEnter) {
        // TODO Auto-generated method stub
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);
            JavascriptExecutor jsExec = (JavascriptExecutor) getDriver();
            for (MobileElement element : elements) {

                if (visibilityofWebelement(element)) {
                    jsExec.executeScript("arguments[0].value='" + textToEnter + "'", element);
                    bFlag = true;
                    break;
                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL,
                        "Text '" + textToEnter + "' not entered successfuly into '" + elementName);

            }

        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Timeout Exception occured while entering text : '" + textToEnter + "' into '" + elementName,
                    t);

        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while entering text : '" + textToEnter + "' into '" + elementName, e);
        }
    }

    @Override
    public boolean clearTextBox(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);
            for (MobileElement element : elements) {
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
            Reporting.getLogger().log(LogStatus.FAIL, "Exception on clear text from " + elementName, e);
        }
        return bFlag;
    }

    @Override
    public boolean isTextBoxEditable(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {

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
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while checking Text box is editable for'" + elementName, e);
        }

        return bFlag;
    }

    @Override
    public JavascriptExecutor getJavaScriptExecutor() {
        // TODO Auto-generated method stub
        return (JavascriptExecutor) getDriver();
    }

    @Override
    public void executeJavaScript(String javaScript) {
        // TODO Auto-generated method stub
        try {
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
            js.executeScript(javaScript);
        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while executing javascript on browser", e);
        }
    }

    @Override
    public void selectDropdownWithIndex(String elementName, String property, int index) {
        // TODO Auto-generated method stub
        boolean bFlag = false;

        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {

                if (visibilityofWebelement(element)) {

                    highlighElement(element);
                    // scrolltoCenterElement(element);
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
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while selecting option for dropdown '" + elementName, e);
        }
    }

    @Override
    public boolean selectDropdownWithVisibleText(String elementName, String property, String visibleText) {
        // TODO Auto-generated method stub
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);
            for (MobileElement element : elements) {
                if (visibilityofWebelement(element)) {
                    highlighElement(element);
                    // scrolltoCenterElement(element);
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

        } catch (Exception e) {

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while selecting option '" + visibleText + "' from dropdown '" + elementName);
        }

        return bFlag;
    }

    @Override
    public String getDropdownSelectedVisibleText(String elementName, String property) {
        String text = "";
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {

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

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while finding the dropdown selected visible text for element " + elementName, e);
        }

        return text;
    }

    @Override
    public List<String> getDropdownOptions(String elementName, String property) {
        // TODO Auto-generated method stub
        boolean bFlag = false;
        List<String> dropDownOptions = new ArrayList<>();
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (WebElement element : elements) {

                if (visibilityofWebelement((MobileElement) element)) {
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

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while finding the dropdown options from " + elementName, e);
        }

        return dropDownOptions;
    }

    @Override
    public boolean doubleClick(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {

                MobileElement ele = verifyElementClickable(element);
                if (ele != null) {
                    // scrolltoCenterElement(element);

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
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while performing double click on element " + elementName, e);
        }

        return bFlag;
    }

    @Override
    public boolean rightClick(String elementName, String property) {
        // TODO Auto-generated method stub
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {

                MobileElement ele = verifyElementClickable(element);
                if (ele != null) {

                    // scrolltoCenterElement(element);

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
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while performing right click on element " + elementName, e);
        }

        return bFlag;
    }

    @Override
    public boolean pressEnter(String elementName, String property) {
        // TODO Auto-generated method stub
//		getDriver().findElement(MobileBy.xpath(property)).sendKeys(AndroidKey.ENTER);
//        AndroidDriver driver = (AndroidDriver) getDriver();
//        driver.pressKey(new KeyEvent().withKey(AndroidKey.ENTER));
        List<MobileElement> elements = getWebElementList(property, elementName);
        for (MobileElement element : elements) {
            // scrolltoCenterElement(element);
            element.sendKeys("\n");
        }
        return false;

    }

    @Override
    public String getText(String elementName, String property) {
        boolean bFlag = false;
        String text = null;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {
                // scrolltoCenterElement(element);
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
            Reporting.getLogger().log(LogStatus.FAIL, "Exception in fetching text from element " + elementName, e);
        }

        return text;
    }

//    private void scrolltoCenterElement(WebElement element) {
//        JavascriptExecutor jsExec = (JavascriptExecutor) getDriver();
//
//        try {
//            Map<String, Object> params = new HashMap<>();
//            params.put("direction", "down");
//            params.put("element", element);
//            jsExec.executeScript("mobile: scroll", params);
//
////            jsExec.executeScript("arguments[0].scrollIntoView({block: \"center\"});", element);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void scrollToElement(String property, String elementName) {
        boolean bFlag = false;
        List<MobileElement> elements = getWebElementList(elementName, property);
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
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while performing scroll action to element '" + elementName + "'", e);
            e.printStackTrace();
        }
    }

    @Override
    public String getTextFromTextboxOrDropbox(String elementName, String property) {
        // TODO Auto-generated method stub
        boolean bFlag = false;
        String text = null;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {

                if (visibilityofWebelement(element)) {
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
            Reporting.getLogger().log(LogStatus.FAIL, "Exception in fetching text from element " + elementName, e);
        }
        return text;
    }

    @Override
    public String getValueOfAttribute(String elementName, String property, String attribute) {
        // TODO Auto-generated method stub
        boolean bFlag = false;
        String text = null;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {

                if (visibilityofWebelement(element)) {
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
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception while fetching value of attribute '" + attribute + "' for element " + elementName, e);
        }

        return null;
    }

    @Override
    public void quitDriver() {
        // TODO Auto-generated method stub
        if (getDriver() != null) {
            getDriver().quit();
        }
    }

    @Override
    public boolean isTextPresent(String elementName, String property, String text) {
        // TODO Auto-generated method stub
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {

                if (visibilityofWebelement(element)) {
                    highlighElement(element);
                    String elemntText = element.getText();
                    if (elemntText.equals(text)) {
                        bFlag = true;
                    }

                    break;
                }
            }

            if (bFlag) {
                Reporting.getLogger().log(LogStatus.INFO, "Text '" + text + "' present in element" + elementName);
            } else {

                Reporting.getLogger().log(LogStatus.INFO, "Text '" + text + "' not present in element" + elementName);
            }

            return bFlag;
        } catch (TimeoutException timeoutException) {

            Reporting.getLogger().log(LogStatus.INFO,
                    "Exception while verifying Text '" + text + "' present in element" + elementName, timeoutException);
        } catch (Exception e) {

            Reporting.getLogger().log(LogStatus.INFO,
                    "Exception while verifying Text '" + text + "' present in element" + elementName, e);
        }

        return false;
    }

    @Override
    public List getWebElementsList(String elementName, String property) {
        // TODO Auto-generated method stub
//        Map<String, List<String>> locatorsMapValues = getLocatorsMap(property);
//        List<MobileElement> mobileElements = getDriver().findElements(By.xpath(property));
//        return mobileElements;

        try {
            List<MobileElement> elements = new ArrayList<>();

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

                Reporting.getLogger().log(LogStatus.FAIL, "Failed to get web element list for locator '" + elementName);
            } else {

                Reporting.getLogger().log(LogStatus.PASS, "Web element list for locator " + elementName + " found.");
                return elements;
            }
        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception while fetching Web element list for locator '" + elementName, e);

        }
        return null;
//        throw new RuntimeException("Error while processing WebElement " + elementName);
    }

    @Override
    public List<String> getTextFromWebElementList(String elementName, String property) {
        // TODO Auto-generated method stub
        List<String> textValues = new ArrayList<>();

//        List<MobileElement> mobileElements = getDriver().findElements(By.xpath(property));
        List<MobileElement> mobileElements = getWebElementsList(elementName, property);
        for (MobileElement element : mobileElements) {
            textValues.add(element.getText());
        }
        return textValues;
    }

    @Override
    public String getPageTitle() {
        try {
            String pageTitle = driver.getTitle();
            return pageTitle;
        } catch (TimeoutException timeoutException) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception while fetching page title ", timeoutException);

        } catch (Exception e) {

            Reporting.getLogger().log(LogStatus.FAIL, "Exception while fetching page title ", e);
        }

        return null;
    }

    public boolean isElementSelected(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {

                if (visibilityofWebelement(element)) {
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

            Reporting.getLogger().log(LogStatus.INFO,
                    "Exception occurred while checking the existence of element '" + elementName, e);
        }

        return false;
    }

    @Override
    public boolean isElementExists(String elementName, String property) {
        // TODO Auto-generated method stub
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {

                if (visibilityofWebelement(element)) {
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

                Reporting.getLogger().log(LogStatus.FAIL, "Element '" + elementName + "' does not exists");
            }

            return bFlag;
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Timeout Exception occured while checking the existance of element '" + elementName, t);
        } catch (Exception e) {

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while checking the existance of element '" + elementName, e);
        }

        return false;
    }

    @Override
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
            Reporting.getLogger().log(LogStatus.FAIL, "Expetion occured while accepting Alert", e);
        }

        return bFlag;
    }

    @Override
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
            Reporting.getLogger().log(LogStatus.FAIL, "Exception while canceling the Alert", e);
        }
        return bFlag;
    }

    @Override
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
            Reporting.getLogger().log(LogStatus.FAIL, "Exception while getting text from alert.");
            return null;
        }
    }

    @Override
    public boolean waitUntilElementIsVisible(String elementName, String property, long timeOutInSeconds) {
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(elementName, property, timeOutInSeconds);

            for (MobileElement element : elements) {
                highlighElement(element);
                Thread.sleep(300);
                if (existenceofWebelement(element, timeOutInSeconds)) {

                    bFlag = true;

                    break;
                }
            }
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception while locating '" + elementName,
                    t);
        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating '" + elementName, e);
        }
        return bFlag;
    }

    @Override
    public boolean waitUntilElementIsVisible(String elementName, String property, long timeOutInSeconds, boolean toLog) {
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(elementName, property, timeOutInSeconds);

            for (MobileElement element : elements) {
                highlighElement(element);
                Thread.sleep(300);
                if (existenceofWebelement(element, timeOutInSeconds)) {

                    bFlag = true;

                    break;
                }
            }
        } catch (TimeoutException t) {
            if (toLog)
                Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception while locating '" + elementName,
                        t);
        } catch (Exception e) {
            if (toLog)

                Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating '" + elementName, e);
        }
        return bFlag;
    }

    @Override
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
            Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating Element " + elementName, e);
        }
        return bFlag;
    }

    public boolean waitUntilElementIsVisible(String elementName, String property, boolean toLog) {
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(elementName, property, this.timeOutDuration.getSeconds());

            for (MobileElement element : elements) {
                highlighElement(element);
                Thread.sleep(300L);
                if (existenceofWebelement(element, this.timeOutDuration.getSeconds())) {

                    bFlag = true;

                    break;
                }
            }
        } catch (TimeoutException t) {
            if (toLog)
                Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception while locating '" + elementName,
                        t);
        } catch (Exception e) {
            if (toLog)
                Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating '" + elementName, e);
        }
        return bFlag;
    }

    @Override

    public boolean waitUntilElementIsVisible(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(elementName, property, this.timeOutDuration.getSeconds());

            for (MobileElement element : elements) {
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
            Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating '" + elementName, e);
        }
        return bFlag;
    }

    @Override
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
            Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating Element " + elementName, e);
        }
        return bFlag;
    }

    @Override
    public boolean waitUntilElementIsInVisible(String elementName, String property, long waitInMiliSec, boolean toLog) {
        boolean bFlag = false;
        try {
            if (verifyElementInvisibility(property, waitInMiliSec)) {

                bFlag = true;
            } else {

                Reporting.getLogger().log(LogStatus.FAIL,
                        "Element '" + elementName + "' did not get invisible in specified time");
            }

        } catch (TimeoutException t) {
            if (toLog)
                Reporting.getLogger().log(LogStatus.FAIL, "Timeout Exception while locating Element " + elementName,
                        t);
        } catch (Exception e) {
            if (toLog)

                Reporting.getLogger().log(LogStatus.FAIL, "Exception while locating Element " + elementName, e);
        }
        return bFlag;
    }

    @Override
    public boolean waitUntilElementToBeClickable(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {
                highlighElement(element);
                MobileElement ele = verifyElementClickable(element);
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

        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Element '" + elementName + "' was not clickable");
        }
        return bFlag;
    }

    @Override
    public boolean areElementsPresent(String elementName, String property) {
        if (getWebElementsList(elementName, property).size() != 0) {
            Reporting.getLogger().log(LogStatus.PASS, "Elements are present for the locator " + elementName);
            return true;
        }
        Reporting.getLogger().log(LogStatus.FAIL, "Elements are not present for the locator " + elementName);
        return false;
    }

    @Override
    public String getCurrentWindowHandle() {
        String windowHandle = null;

        try {
            windowHandle = getDriver().getWindowHandle();
        } catch (TimeoutException t) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while getting the current window handle",
                    t);
        } catch (Exception e) {

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while getting the current window handle", e);
        }
        return windowHandle;
    }

    @Override
    public Set<String> getAllWindowHandles() {
        try {
            Set<String> windowHandles = null;

            windowHandles = getDriver().getWindowHandles();
            return windowHandles;
        } catch (TimeoutException t) {
            return null;
        } catch (Exception e) {

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while getting the all window handle", e);
            return null;
        }
    }

    @Override
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
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception while switching to window with handle '" + previousWindowHandle + "'", e);
        }

        return bFlag;
    }

    @Override
    public void switchToNewWindow(int i) {
        try {
            Set<String> windows = getAllWindowHandles();
            getDriver().switchTo().window(windows.toArray(new String[windows.size()])[i]);
            Reporting.getLogger().log(LogStatus.PASS, "Switched to new window");
        } catch (TimeoutException timeoutException) {

        } catch (Exception e) {

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while switching to new window ", e);
        }

    }

    @Override
    public void closeWindow(int i) {
        try {
            Set<String> windows = getDriver().getWindowHandles();
            getDriver().switchTo().window(windows.toArray(new String[windows.size()])[i]).close();
        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while closing the window ", e);
        }

    }

    @Override
    public void refreshPage() {
        try {
            getDriver().navigate().refresh();
        } catch (TimeoutException timeoutException) {

        } catch (Exception e) {

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while refreshing the page", e);
        }

    }

    @Override
    public void navigateToPreviousPage() {
        try {
            getDriver().navigate().back();
        } catch (Exception e) {

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while navigating to previous page", e);
        }

    }

    @Override
    public void mouseHover(String elementName, String property) {
        boolean bFlag = false;
        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {

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

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while performing mouse hover operation over element '" + elementName, e);
        }

    }

    @Override
    public boolean mouseHoverClickChild(String parentproperty, String childproperty) {
        boolean bFlag = false;

        try {
            List<MobileElement> parentElements = getWebElementList(parentproperty, null);
            List<MobileElement> childElements = getWebElementList(childproperty, null);

            for (MobileElement element : parentElements) {

                if (visibilityofWebelement(element)) {
                    for (MobileElement childElement : childElements) {
                        if (visibilityofWebelement(childElement)) {
                            // // scrolltoCenterElement(element);

                            Actions action = new Actions(getDriver());
                            action.moveToElement(element).moveToElement(childElement).click().build().perform();
                            bFlag = true;
                        }

                    }
                }
            }
        } catch (Exception e) {

            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occurred while clicking child element after mouse hover", e);
        }

        return bFlag;
    }

    @Override
    public String getPageSource() {
        String pageSource = null;

        try {
            pageSource = getDriver().getPageSource();
        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occurred while finding the page source", e);
        }

        return pageSource;
    }

    @Override
    public void deleteAllCookies() {
        // TODO Auto-generated method stub
        try {
            getDriver().manage().deleteAllCookies();
            Thread.sleep(3500L);
        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while deleting the cookies", e);
        }
    }

    @Override
    public String getCurrentUrl() {
        // TODO Auto-generated method stub
        try {
            String currenturl = getDriver().getCurrentUrl();
            return currenturl;
        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while getting the current URL", e);
            return null;
        }
    }

    @Override
    public void switchToIframe() {
        // TODO Auto-generated method stub
        try {
            (new WebDriverWait(getDriver(), 15L))
                    .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.tagName("iframe")));
        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while switching to iframe", e);
        }
    }

    @Override
    public void switchToIframeById(String iFrameId) {
        // TODO Auto-generated method stub
        try {
            (new WebDriverWait(getDriver(), 15L))
                    .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id(iFrameId)));
        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while switching to iframe with id" + iFrameId,
                    e);
        }
    }

    @Override
    public void switchToIframeByIndex(int frameIndex) {
        // TODO Auto-generated method stub
        try {
            (new WebDriverWait(getDriver(), 15L))
                    .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.tagName("iframe")));
        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while switching to iframe", e);
        }
    }

    @Override
    public boolean switchToIframe(String elementName, String property) {
        // TODO Auto-generated method stub
        boolean bFlag = false;

        try {
            List<MobileElement> elements = getWebElementList(property, elementName);

            for (MobileElement element : elements) {

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
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while switching to iframe " + elementName, e);
        }
        return bFlag;
    }

    @Override
    public void deselectIframe() {
        // TODO Auto-generated method stub
        try {
            getDriver().switchTo().defaultContent();
            Reporting.getLogger().log(LogStatus.PASS, "Deselect the ifame");
        } catch (TimeoutException timeoutException) {

        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while deselecting the iframe", e);
        }
    }

    @Override
    public void waitFor(int enterMiliSeconds) {
        // TODO Auto-generated method stub
        try {
            Thread.sleep(enterMiliSeconds);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

    @Override
    public void waitForPageLoad() {
        // TODO Auto-generated method stub
        try {

            getDriver().manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            new WebDriverWait(getDriver(), 30).until((ExpectedCondition<Boolean>) wd -> ((JavascriptExecutor) wd)
                    .executeScript("return document.readyState").equals("complete"));
        } catch (Exception e) {

//            Reporting.getLogger().log(LogStatus.FAIL, "Exception occurred while waiting for page load", e);
        }
    }

    @Override
    public void closeCurrentWindow() {
        // TODO Auto-generated method stub
        try {
            getDriver().close();
        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while closing current window", e);
        }
    }

    @Override
    public boolean verifyTitle(String title) {
        // TODO Auto-generated method stub
        if (getDriver().getTitle().equalsIgnoreCase(title)) {
            Reporting.getLogger().log(LogStatus.PASS, "Expected title " + title + " is equal to current title: " + getDriver().getTitle());
            return true;
        } else {
            Reporting.getLogger().log(LogStatus.FAIL, "Expected title " + title + " is equal to current title: " + getDriver().getTitle());
            return false;

        }
    }

    @Override
    public String getCssValue(String elementName, String property, String cssAttribute) {
        boolean bFlag = false;
        String value = null;
        try {
            List<MobileElement> elements = getWebElementList(property, null);

            for (MobileElement element : elements) {

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
            Reporting.getLogger().log(LogStatus.FAIL, "Exception in fetching text from element " + elementName, e);
        }

        return value;
    }

    @Override
    public String getExceptionDetails(Exception e) {
        return e.getLocalizedMessage();
    }

    @Override
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
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while fetching pag load time for page " + pageName, e);
        }

        return null;
    }

    @Override
    public void highlighElement(WebElement element) {

    }


    /**
     * verify whether the element is clickable:-
     */
    private MobileElement verifyElementClickable(MobileElement element) {

        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), 40);
            return (MobileElement) wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {

            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while verifying element is clickable", e);
            throw e;

        }

    }

    private boolean visibilityofWebelement(MobileElement element) {

        try {
            FluentWait<MobileElement> _waitForElement = new FluentWait<MobileElement>(element);
            _waitForElement.pollingEvery(pollingDuration);
            _waitForElement.withTimeout(timeOutDuration);
            _waitForElement.ignoring(NoSuchElementException.class);
            _waitForElement.ignoring(StaleElementReferenceException.class);
            _waitForElement.ignoring(ElementNotVisibleException.class);

            Function<MobileElement, Boolean> elementVisibility = new Function<MobileElement, Boolean>() {

                public Boolean apply(MobileElement element) {
                    // TODO Auto-generated method stub

                    return element.isDisplayed();
                }

            };

            return _waitForElement.until(elementVisibility);

        } catch (Exception e) {
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while verifying the visibility of an element ",
                    e);

        }

        return false;
    }

    @SuppressWarnings("unchecked")
//    private WebElement findElement(String locator, String locatorValue, FluentWait<WebDriver> driverWait) {
//
//        WebElement element = null;
//        final String locVal = locatorValue;
//
//        try {
//            Stopwatch stopwatch = null;
//            if (locator.equalsIgnoreCase("id")) {
//                if (locatorTimeFlag) {
//                    stopwatch = Stopwatch.createStarted();
//                }
//                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            return getDriver().findElement(By.id(locVal));
//                        }
//
//                    };
//                    element = driverWait.until(waitForElement);
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "id");
//                        jsonObj.put("isWorking", "Yes");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MILLISECONDS));
//                    }
//                } catch (Exception e) {
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "id");
//                        jsonObj.put("isWorking", "No");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
//                    }
//                    throw e;
//                }
//            }
//            if (locator.equalsIgnoreCase("xpath")) {
//                if (locatorTimeFlag) {
//                    stopwatch = Stopwatch.createStarted();
//                }
//                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//
//                        public WebElement apply(WebDriver driverWait) {
//                            // TODO Auto-generated method stub
//                            return getDriver().findElement(By.xpath(locVal));
//                        }
//
//                    };
//                    element = driverWait.until(waitForElement);
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "xpath");
//                        jsonObj.put("isWorking", "Yes");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
//                    }
//                } catch (
//
//                        Exception e) {
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "xpath");
//                        jsonObj.put("isWorking", "No");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
//                    }
//                    throw e;
//                }
//            }
//            if (locator.equalsIgnoreCase("className")) {
//                if (locatorTimeFlag) {
//                    stopwatch = Stopwatch.createStarted();
//                }
//                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            // TODO Auto-generated method stub
//                            return getDriver().findElement(By.className(locVal));
//                        }
//
//                    };
//                    element = driverWait.until(waitForElement);
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "className");
//                        jsonObj.put("isWorking", "Yes");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
//                    }
//                } catch (Exception e) {
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "className");
//                        jsonObj.put("isWorking", "No");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
//                    }
//                    throw e;
//                }
//            }
//            if (locator.equalsIgnoreCase("name")) {
//                if (locatorTimeFlag) {
//                    stopwatch = Stopwatch.createStarted();
//                }
//                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            // TODO Auto-generated method stub
//                            return getDriver().findElement(By.name(locVal));
//                        }
//
//                    };
//                    element = driverWait.until(waitForElement);
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "name");
//                        jsonObj.put("isWorking", "Yes");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
//                    }
//                } catch (Exception e) {
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "name");
//                        jsonObj.put("isWorking", "No");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
//                    }
//                    throw e;
//                }
//            }
//            if (locator.equalsIgnoreCase("tagName")) {
//                if (locatorTimeFlag) {
//                    stopwatch = Stopwatch.createStarted();
//                }
//                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            // TODO Auto-generated method stub
//                            return getDriver().findElement(By.tagName(locVal));
//                        }
//
//                    };
//                    element = driverWait.until(waitForElement);
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "tagName");
//                        jsonObj.put("isWorking", "Yes");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
//                    }
//                } catch (Exception e) {
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "tagName");
//                        jsonObj.put("isWorking", "No");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
//                    }
//                    throw e;
//                }
//            }
//            if (locator.equalsIgnoreCase("css")) {
//                if (locatorTimeFlag) {
//                    stopwatch = Stopwatch.createStarted();
//                }
//                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            // TODO Auto-generated method stub
//                            return getDriver().findElement(By.cssSelector(locVal));
//                        }
//
//                    };
//                    element = driverWait.until(waitForElement);
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "css");
//                        jsonObj.put("isWorking", "Yes");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
//                    }
//                } catch (Exception e) {
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "css");
//                        jsonObj.put("isWorking", "No");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
//                    }
//                    throw e;
//                }
//            }
//
//            if (locator.equalsIgnoreCase("linkText")) {
//                if (locatorTimeFlag) {
//                    stopwatch = Stopwatch.createStarted();
//                }
//                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            // TODO Auto-generated method stub
//                            return getDriver().findElement(By.linkText(locVal));
//                        }
//
//                    };
//                    element = driverWait.until(waitForElement);
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "linkText");
//                        jsonObj.put("isWorking", "Yes");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
//                    }
//                } catch (Exception e) {
//                    if (locatorTimeFlag) {
//                        stopwatch.stop();
//                        jsonObj.put("Locator", "linkText");
//                        jsonObj.put("isWorking", "No");
//                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
//                    }
//                    throw e;
//                }
//            }
//            jsonObj.put("value", locVal);
//            return element;
//
//        } catch (Exception e) {
//            jsonObj.put("value", locVal);
//            Reporting.getLogger().log(LogStatus.INFO, " Locator " + locator + ":" + locatorValue + " not found :", e);
//            throw e;
//
//        }
//
//        // return null;
//    }
    private MobileElement findElement(String locator, String locatorValue, FluentWait<AppiumDriver> driverWait) {

        MobileElement element = null;
        final String locVal = locatorValue;

        try {
            Stopwatch stopwatch = null;
            if (locator.equalsIgnoreCase("id")) {
                if (locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
                    Function<AppiumDriver, MobileElement> waitForElement = new Function<AppiumDriver, MobileElement>() {
                        public MobileElement apply(AppiumDriver driverWait) {
//                            return getDriver().findElement(By.id(locVal));
                            return driver.findElement(By.id(locVal));
                        }

                    };
                    element = driverWait.until(waitForElement);
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "id");
                        jsonObj.put("isWorking", "Yes");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                    }
                } catch (Exception e) {
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "id");
                        jsonObj.put("isWorking", "No");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                    throw e;
                }
            }
            if (locator.equalsIgnoreCase("mobileid")) {
                if (locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
                    Function<AppiumDriver, MobileElement> waitForElement = new Function<AppiumDriver, MobileElement>() {
                        public MobileElement apply(AppiumDriver driverWait) {
//                            return getDriver().findElement(By.id(locVal));
                            return driver.findElement(MobileBy.id(locVal));
                        }

                    };
                    element = driverWait.until(waitForElement);
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "id");
                        jsonObj.put("isWorking", "Yes");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                    }
                } catch (Exception e) {
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "id");
                        jsonObj.put("isWorking", "No");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                    throw e;
                }
            }
            if (locator.equalsIgnoreCase("accessibilityId")) {
                if (locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
                    Function<AppiumDriver, MobileElement> waitForElement = new Function<AppiumDriver, MobileElement>() {
                        public MobileElement apply(AppiumDriver driverWait) {
//                            return getDriver().findElement(By.id(locVal));
                            return driver.findElement(MobileBy.AccessibilityId(locVal));
                        }

                    };
                    element = driverWait.until(waitForElement);
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "AccessibilityId");
                        jsonObj.put("isWorking", "Yes");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MILLISECONDS));
                    }
                } catch (Exception e) {
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "id");
                        jsonObj.put("isWorking", "No");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                    throw e;
                }
            }
            if (locator.equalsIgnoreCase("xpath")) {
                if (locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
                    Function<AppiumDriver, MobileElement> waitForElement = new Function<AppiumDriver, MobileElement>() {
                        public MobileElement apply(AppiumDriver driverWait) {
                            return driver.findElement(By.xpath(locVal));
                        }

                    };
                    element = driverWait.until(waitForElement);
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "xpath");
                        jsonObj.put("isWorking", "Yes");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                } catch (

                        Exception e) {
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "xpath");
                        jsonObj.put("isWorking", "No");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                    throw e;
                }
            }
            if (locator.equalsIgnoreCase("className")) {
                if (locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {

                    Function<AppiumDriver, MobileElement> waitForElement = new Function<AppiumDriver, MobileElement>() {
                        public MobileElement apply(AppiumDriver driverWait) {
                            return driver.findElement(By.className(locVal));
                        }

                    };
                    element = driverWait.until(waitForElement);
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "className");
                        jsonObj.put("isWorking", "Yes");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                } catch (Exception e) {
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "className");
                        jsonObj.put("isWorking", "No");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                    throw e;
                }
            }
            if (locator.equalsIgnoreCase("name")) {
                if (locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {

                    Function<AppiumDriver, MobileElement> waitForElement = new Function<AppiumDriver, MobileElement>() {
                        public MobileElement apply(AppiumDriver driverWait) {
                            return driver.findElement(By.name(locVal));
                        }

                    };
                    element = driverWait.until(waitForElement);
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "name");
                        jsonObj.put("isWorking", "Yes");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                } catch (Exception e) {
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "name");
                        jsonObj.put("isWorking", "No");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                    throw e;
                }
            }
            if (locator.equalsIgnoreCase("tagName")) {
                if (locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            // TODO Auto-generated method stub
//                            return getDriver().findElement(By.tagName(locVal));
//                        }
//
//                    };
                    Function<AppiumDriver, MobileElement> waitForElement = new Function<AppiumDriver, MobileElement>() {
                        public MobileElement apply(AppiumDriver driverWait) {
                            return driver.findElement(By.tagName(locVal));
                        }

                    };
                    element = driverWait.until(waitForElement);
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "tagName");
                        jsonObj.put("isWorking", "Yes");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                } catch (Exception e) {
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "tagName");
                        jsonObj.put("isWorking", "No");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                    throw e;
                }
            }
            if (locator.equalsIgnoreCase("css")) {
                if (locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            // TODO Auto-generated method stub
//                            return getDriver().findElement(By.cssSelector(locVal));
//                        }
//
//                    };
                    Function<AppiumDriver, MobileElement> waitForElement = new Function<AppiumDriver, MobileElement>() {
                        public MobileElement apply(AppiumDriver driverWait) {
                            return driver.findElement(By.cssSelector(locVal));
                        }

                    };
                    element = driverWait.until(waitForElement);
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "css");
                        jsonObj.put("isWorking", "Yes");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                } catch (Exception e) {
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "css");
                        jsonObj.put("isWorking", "No");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                    throw e;
                }
            }

            if (locator.equalsIgnoreCase("linkText")) {
                if (locatorTimeFlag) {
                    stopwatch = Stopwatch.createStarted();
                }
                try {
//                    Function<WebDriver, WebElement> waitForElement = new Function<WebDriver, WebElement>() {
//                        public WebElement apply(WebDriver driverWait) {
//                            // TODO Auto-generated method stub
//                            return getDriver().findElement(By.linkText(locVal));
//                        }
//
//                    };
                    Function<AppiumDriver, MobileElement> waitForElement = new Function<AppiumDriver, MobileElement>() {
                        public MobileElement apply(AppiumDriver driverWait) {
                            return driver.findElement(By.linkText(locVal));
                        }

                    };
                    element = driverWait.until(waitForElement);
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "linkText");
                        jsonObj.put("isWorking", "Yes");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                } catch (Exception e) {
                    if (locatorTimeFlag) {
                        stopwatch.stop();
                        jsonObj.put("Locator", "linkText");
                        jsonObj.put("isWorking", "No");
                        jsonObj.put("time", stopwatch.elapsed(TimeUnit.MICROSECONDS));
                    }
                    throw e;
                }
            }
            jsonObj.put("value", locVal);
            return element;

        } catch (Exception e) {
            jsonObj.put("value", locVal);
            Reporting.getLogger().log(LogStatus.INFO, " Locator " + locator + ":" + locatorValue + " not found :", e);
            throw e;

        }

        // return null;
    }

    /**
     * return webelement of locator:-
     */
    public MobileElement getWebelement(String locator, String locatorValue) {

        try {
            FluentWait<AppiumDriver> driverWait = new FluentWait<AppiumDriver>(getDriver());
            driverWait.pollingEvery(pollingDuration);
            driverWait.withTimeout(timeOutDuration);
            driverWait.ignoring(NoSuchElementException.class);
            driverWait.ignoring(StaleElementReferenceException.class);
            driverWait.ignoring(ElementNotVisibleException.class);

            return findElement(locator, locatorValue, driverWait);

        } catch (Exception e) {
            // e.printStackTrace();
            return null;
        }

    }

    private MobileElement getWebelement(String locator, String locatorValue, long timeOutInSeconds) {

        FluentWait<AppiumDriver> driverWait = new FluentWait<AppiumDriver>(getDriver());
        driverWait.pollingEvery(pollingDuration);
        Duration timeout = Duration.of(timeOutInSeconds, ChronoUnit.SECONDS);
        driverWait.withTimeout(timeout);
        driverWait.ignoring(NoSuchElementException.class);
        driverWait.ignoring(StaleElementReferenceException.class);
        driverWait.ignoring(ElementNotVisibleException.class);

        return findElement(locator, locatorValue, driverWait);
    }

    @SuppressWarnings("unchecked")
//    private List<MobileElement> getWebElementList(String property, String elementName) {
//        errorMsg = null;
//        boolean bFlag = false;
//        boolean locatorFlag = false;
//
//        jsonArray = new JSONArray();
//        Map<String, List<String>> locatorsMapValues = getLocatorsMap(property);
//        List<MobileElement> webElements = new ArrayList<>();
//        try {
//            for (String locator : locatorsMapValues.keySet()) {
//
//                if (!locatorTimeFlag) {
//                    if (bFlag)
//                        break;
//                }
//                List<String> locatorVal = locatorsMapValues.get(locator);
//                for (String locValue : locatorVal) {
//                    jsonObj = new JSONObject();
//                    errorMsg = errorMsg + " Locator Type : " + locator + " and Locator Value : " + locValue;
//                    WebElement element = getWebelement(locator, locValue);
//                    if (element != null) {
//                        webElements.add(element);
//                        bFlag = true;
//                        if (locatorTimeFlag) {
//                            if (!locatorFlag) {
//                                jsonObj.put("isUsed", "Yes");
//                                locatorFlag = true;
//                            } else {
//                                jsonObj.put("isUsed", "No");
//                            }
//                        }
//                    } else {
//                        if (locatorTimeFlag) {
//                            jsonObj.put("isUsed", "No");
//                        }
//                    }
//                    jsonArray.add(jsonObj);
//                }
//                errorMsg = errorMsg + " not found ";
//
//            }
//            if (locatorTimeFlag) {
//                Reporting.getLogger().log(jsonArray.toString(), elementName);
//            }
//            return webElements;
//        } catch (Exception e) {
//            System.out.println(e);
//            e.printStackTrace();
//            throw e;
//        }
//    }
    private List<MobileElement> getWebElementList(String property, String elementName) {
        errorMsg = null;
        boolean bFlag = false;
        boolean locatorFlag = false;

        jsonArray = new JSONArray();
        Map<String, List<String>> locatorsMapValues = getLocatorsMap(property);
        List<MobileElement> webElements = new ArrayList<>();
        try {
            for (String locator : locatorsMapValues.keySet()) {

                if (!locatorTimeFlag) {
                    if (bFlag)
                        break;
                }
                List<String> locatorVal = locatorsMapValues.get(locator);
                for (String locValue : locatorVal) {
                    jsonObj = new JSONObject();
                    errorMsg = errorMsg + " Locator Type : " + locator + " and Locator Value : " + locValue;
                    MobileElement element = getWebelement(locator, locValue);
                    if (element != null) {
                        webElements.add(element);
                        bFlag = true;
                        if (locatorTimeFlag) {
                            if (!locatorFlag) {
                                jsonObj.put("isUsed", "Yes");
                                locatorFlag = true;
                            } else {
                                jsonObj.put("isUsed", "No");
                            }
                        }
                    } else {
                        if (locatorTimeFlag) {
                            jsonObj.put("isUsed", "No");
                        }
                    }
                    jsonArray.add(jsonObj);
                }
                errorMsg = errorMsg + " not found ";

            }
            if (locatorTimeFlag) {
                Reporting.getLogger().log(jsonArray.toString(), elementName);
            }
            return webElements;
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private List<MobileElement> getWebElementList(String elementName, String properties, long timeOutInSeconds) {
        jsonArray = new JSONArray();
        boolean locatorFlag = false;
        // TODO Auto-generated method stub
        // Map<String, String> descriptionMap = getDescription(elementName,
        // stepDescription);
        try {
            Map<String, List<String>> locatorsMapValues = getLocatorsMap(properties);

            List<MobileElement> webElements = new ArrayList();
            for (String locator : locatorsMapValues.keySet()) {

                List<String> locatorVal = locatorsMapValues.get(locator);
                for (String locValue : locatorVal) {
                    jsonObj = new JSONObject();
                    errorMsg = errorMsg + "Locator Type : " + locator + " and locator Value : " + locValue;
                    MobileElement element = getWebelement(locator, locValue, timeOutInSeconds);
                    if (element != null) {
                        webElements.add(element);
                        if (locatorTimeFlag) {
                            if (!locatorFlag) {
                                jsonObj.put("isUsed", "Yes");
                                locatorFlag = true;
                            } else {
                                jsonObj.put("isUsed", "No");
                            }
                        }
                    } else {
                        if (locatorTimeFlag) {
                            jsonObj.put("isUsed", "No");
                        }
                    }
                    jsonArray.add(jsonObj);
                }
                errorMsg = errorMsg + " not found ";

            }
            if (locatorTimeFlag) {
                Reporting.getLogger().log(jsonArray.toString(), elementName);
            }
            return webElements;

        } catch (TimeoutException t) {
            return null;
        } catch (Exception e) {
            throw e;

        }

    }

    private Map<String, List<String>> getLocatorsMap(String properties) {
        Map<String, List<String>> locatorsMapValues = new HashMap<String, List<String>>();
        try {

            // Split the different locators based upon Pipe symbols:-
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
            System.out.println(e);
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while getting locators from properties", e);
        }

        return locatorsMapValues;
    }

    private List<MobileElement> getWebElementsOfElement(String locator, String locatorValue) {

        List<MobileElement> elements = null;
        final String locVal = locatorValue;
        FluentWait<AppiumDriver> driverWait = new FluentWait<AppiumDriver>(getDriver());
        driverWait.pollingEvery(pollingDuration);
        driverWait.withTimeout(timeOutDuration);
        driverWait.ignoring(NoSuchElementException.class);
        driverWait.ignoring(StaleElementReferenceException.class);

        try {
            if (locator.equalsIgnoreCase("id")) {

                Function<WebDriver, List<MobileElement>> waitForElement = new Function<WebDriver, List<MobileElement>>() {
                    public List<MobileElement> apply(WebDriver driverWait) {
                        // TODO Auto-generated method stub
                        return getDriver().findElements(By.id(locVal));
                    }

                };
                elements = driverWait.until(waitForElement);
            }
            if (locator.equalsIgnoreCase("mobileid")) {

                Function<WebDriver, List<MobileElement>> waitForElement = new Function<WebDriver, List<MobileElement>>() {
                    public List<MobileElement> apply(WebDriver driverWait) {
                        // TODO Auto-generated method stub
                        return getDriver().findElements(MobileBy.id(locVal));
                    }

                };
                elements = driverWait.until(waitForElement);
            }
            if (locator.equalsIgnoreCase("accessibilityId")) {
                Function<WebDriver, List<MobileElement>> waitForElement = new Function<WebDriver, List<MobileElement>>() {
                    public List<MobileElement> apply(WebDriver driverWait) {
                        // TODO Auto-generated method stub
                        return getDriver().findElements(MobileBy.AccessibilityId(locVal));
                    }

                };
                elements = driverWait.until(waitForElement);
            }
            if (locator.equalsIgnoreCase("xpath")) {

                Function<WebDriver, List<MobileElement>> waitForElement = new Function<WebDriver, List<MobileElement>>() {

                    public List<MobileElement> apply(WebDriver driverWait) {
                        // TODO Auto-generated method stub
                        return getDriver().findElements(By.xpath(locVal));
                    }

                };
                elements = driverWait.until(waitForElement);

            }
            if (locator.equalsIgnoreCase("className")) {

                Function<WebDriver, List<MobileElement>> waitForElement = new Function<WebDriver, List<MobileElement>>() {
                    public List<MobileElement> apply(WebDriver driverWait) {
                        // TODO Auto-generated method stub
                        return getDriver().findElements(By.className(locVal));
                    }

                };
                elements = driverWait.until(waitForElement);

            }
            if (locator.equalsIgnoreCase("name")) {
                Function<WebDriver, List<MobileElement>> waitForElement = new Function<WebDriver, List<MobileElement>>() {
                    public List<MobileElement> apply(WebDriver driverWait) {
                        // TODO Auto-generated method stub
                        return getDriver().findElements(By.name(locVal));
                    }

                };
                elements = driverWait.until(waitForElement);
            }
            if (locator.equalsIgnoreCase("tagName")) {

                Function<WebDriver, List<MobileElement>> waitForElement = new Function<WebDriver, List<MobileElement>>() {
                    public List<MobileElement> apply(WebDriver driverWait) {
                        // TODO Auto-generated method stub
                        return getDriver().findElements(By.tagName(locVal));
                    }

                };
                elements = driverWait.until(waitForElement);

            }
            if (locator.equalsIgnoreCase("css")) {

                Function<WebDriver, List<MobileElement>> waitForElement = new Function<WebDriver, List<MobileElement>>() {
                    public List<MobileElement> apply(WebDriver driverWait) {
                        // TODO Auto-generated method stub
                        return getDriver().findElements(By.cssSelector(locVal));
                    }

                };
                elements = driverWait.until(waitForElement);

            }

            if (locator.equalsIgnoreCase("linkText")) {

                Function<WebDriver, List<MobileElement>> waitForElement = new Function<WebDriver, List<MobileElement>>() {
                    public List<MobileElement> apply(WebDriver driverWait) {
                        // TODO Auto-generated method stub
                        return getDriver().findElements(By.linkText(locVal));
                    }

                };
                elements = driverWait.until(waitForElement);

            }

            return elements;

        } catch (Exception e) {

            Reporting.getLogger().log(LogStatus.INFO, " Locator " + locator + ":" + locatorValue + " not found :");
        }

        return null;
    }

    @Override
    public boolean switchContext(String context) {
        boolean bFlag = false;
        try {
            for (String contexts : driver.getContextHandles()) {
                if (contexts.contains(context)) {
                    driver.context(contexts);
                    bFlag = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occured while switching context to " + context);
        }
        return bFlag;
    }

    @Override
    public boolean tap(String elementName, String property) {
        boolean bFlag = false;
        List<MobileElement> elements = getWebElementList(property, elementName);
        try {
            for (MobileElement element : elements) {
                MobileElement ele = verifyElementClickable(element);
                if (ele != null) {

                    TouchAction action = new TouchAction(driver);
                    action.tap(new TapOptions().withElement(new ElementOption().withElement(ele))).perform();
                    bFlag = true;
                    break;

                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Failed to tap on Element '" + elementName + "'.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while performing tap action on element '" + elementName + "'", e);
        }
        return bFlag;
    }

    @Override
    public void tap(int xAxis, int yAxis) {
        try {
            TouchAction action = new TouchAction(driver);
            action.tap(PointOption.point(xAxis, yAxis)).perform();

        } catch (Exception e) {
            e.printStackTrace();
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occurred while performing tap on cordinates " + xAxis
                    + " on x axis and " + yAxis + " on y axis", e);
        }

    }

    @Override
    public boolean doubleTap(String elementName, String property) {
        boolean bFlag = false;
        List<MobileElement> elements = getWebElementList(property, elementName);
        try {
            for (MobileElement element : elements) {
                MobileElement ele = verifyElementClickable(element);
                if (ele != null) {

                    TouchAction action = new TouchAction(driver);
                    action.tap(new TapOptions().withTapsCount(2).withElement(new ElementOption().withElement(ele)))
                            .perform();

                    bFlag = true;
                    break;

                }
            }
            if (!bFlag) {
                Reporting.getLogger().log(LogStatus.FAIL, "Failed to tap on Element '" + elementName + "'.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Reporting.getLogger().log(LogStatus.FAIL,
                    "Exception occured while performing tap action on element '" + elementName + "'", e);
        }
        return bFlag;
    }

    @Override
    public void doubleTap(int xAxis, int yAxis) {
        try {
            TouchAction action = new TouchAction(driver);
            action.tap(new TapOptions().withTapsCount(2).withPosition(PointOption.point(xAxis, yAxis))).perform();
        } catch (Exception e) {
            e.printStackTrace();
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occurred while performing tap on cordinates " + xAxis
                    + " on x axis and " + yAxis + " on y axis", e);
        }
    }

    @Override
    public boolean scroll(String fromElement, String toElement) {
        return false;
    }

    @Override
    public void scroll(int fromXCordinate, int fromYCordinate, int toXCordinate, int toYCordinate) {
        try {
            TouchAction action = new TouchAction(driver);
            action.longPress(PointOption.point(fromXCordinate, fromYCordinate))
                    .moveTo(PointOption.point(toXCordinate, toYCordinate)).release().perform();
        } catch (Exception e) {
            e.printStackTrace();
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occurred while performing scroll from ("
                            + fromXCordinate + "," + fromYCordinate + ") to " + " (" + toXCordinate + "," + toYCordinate + ")",
                    e);
        }
    }

    @Override
    public boolean zoom(String elementName, String property) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean performMultiTouch(List<TouchAction> actionList) {
        try {

            MultiTouchAction actions = new MultiTouchAction(driver);
            for (TouchAction action : actionList) {
                actions.add(action);
            }
            actions.perform();
        } catch (Exception e) {
            e.printStackTrace();
            Reporting.getLogger().log(LogStatus.FAIL, "Exception occurred while performing multi actions", e);
        }
        return false;
    }

//    @Override
//    public SeeTestClient getSeeTestClient() {
//
////        return this.seeTestClient;
//        return this.seeTestClient;
//    }

//    @Override
//    public void setSeeTestClient(SeeTestClient seeTestClient) {
//        this.seeTestClient = seeTestClient;
//    }

    private boolean existenceofWebelement(MobileElement element, long timeOutInSeconds) {
        try {
            FluentWait<MobileElement> _waitForElement = new FluentWait<MobileElement>(element);
            _waitForElement.pollingEvery(this.pollingDuration);
            Duration timeout = Duration.of(timeOutInSeconds, ChronoUnit.SECONDS);
            _waitForElement.withTimeout(timeout);
            _waitForElement.ignoring(NoSuchElementException.class);
            _waitForElement.ignoring(StaleElementReferenceException.class);
            _waitForElement.ignoring(ElementNotVisibleException.class);

            Function<MobileElement, Boolean> elementVisibility = new Function<MobileElement, Boolean>() {

                public Boolean apply(MobileElement element) {
                    return Boolean.valueOf(element.isEnabled());
                }
            };

            WebDriverWait wait = new WebDriverWait(getDriver(), 10L);
            wait.until(ExpectedConditions.visibilityOf(element));

            return _waitForElement.until(elementVisibility).booleanValue();
        } catch (Exception exception) {

            return false;
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

            throw new RuntimeException("Key doesn't exists , please verify your key");
        }
        return this.pageTestData.get(key);
    }


}
