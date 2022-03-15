package com.xassure.framework.driver;

//import com.experitest.appium.SeeTestClient;
import io.appium.java_client.TouchAction;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;

public interface Controls {
    WebDriver getDriver();

    void setDriver(String... params);

    void updatePageTestData(String key, Object objectValue);

    public Object getPageTestData(String key);

    PageInfo getPageDetails();

    void setPageDetails(PageInfo pageInfo);

    void launchApplication(String url);

    boolean click(String elementName, String property);

    boolean clickUsingActionBuilder(String elementName, String property);

    boolean clickUsingJavaScriptExecutor(String elementName, String property);

    void enterText(String elementName, String property, String textToEnter);

    public void enterText(String elementName, String property, Keys keys);

    void enterTextUsingJavascriptExecutor(String elementName, String property, String textToEnter);

    boolean clearTextBox(String elementName, String property);

    boolean isTextBoxEditable(String elementName, String property);

    JavascriptExecutor getJavaScriptExecutor();

    void executeJavaScript(String javaScript);

    void selectDropdownWithIndex(String elementName, String property, int valueIndex);

    boolean selectDropdownWithVisibleText(String elementName, String property, String visibleText);

    String getDropdownSelectedVisibleText(String elementName, String property);

    List<String> getDropdownOptions(String elementName, String property);

    boolean doubleClick(String elementName, String property);

    boolean rightClick(String elementName, String property);

    boolean pressEnter(String elementName, String property);

    String getText(String elementName, String property);

    String getTextFromTextboxOrDropbox(String elementName, String property);

    String getValueOfAttribute(String elementName, String property, String attributeName);

    void quitDriver();

    boolean isTextPresent(String elementName, String property, String text);

    List getWebElementsList(String elementName, String property);

    List<String> getTextFromWebElementList(String elementName, String property);

    String getPageTitle();

    boolean isElementExists(String elementName, String property);

    public boolean isElementSelected(String elementName, String property);

    boolean switchAndAcceptAlert();

    boolean switchAndCancelAlert();

    String switchAndGetTextFromAlert();

    boolean waitUntilElementIsVisible(String elementName, String property, long waitInMiliSec);

    public boolean waitUntilElementIsVisible(String elementName, String property, long timeOutInSeconds, boolean toLog);

    boolean waitUntilElementIsInVisible(String elementName, String property);

    boolean waitUntilElementIsVisible(String elementName, String property);

    boolean waitUntilElementIsVisible(String elementName, String property, boolean toLog);

    boolean waitUntilElementIsInVisible(String elementName, String property, long waitInMiliSec);

    boolean waitUntilElementIsInVisible(String elementName, String property, long waitInMiliSec, boolean toLog);

    boolean waitUntilElementToBeClickable(String elementName, String property);

    boolean areElementsPresent(String elementName, String property) throws Exception;

    String getCurrentWindowHandle();

    Set<String> getAllWindowHandles();

    boolean switchToWindow(String windowHandle);

    void switchToNewWindow(int windowIndex);

    void closeWindow(int windowIndex);

    void refreshPage();

    void navigateToPreviousPage();

    void mouseHover(String elementName, String property);

    boolean mouseHoverClickChild(String parentproperty, String childproperty);

    String getPageSource();

    void deleteAllCookies();

    String getCurrentUrl();

    void switchToIframe();

    void switchToIframeById(String frameId);

    void switchToIframeByIndex(int frameIndex);

    boolean switchToIframe(String elementName, String property);

    void deselectIframe();

    void waitFor(int waitInSec);

    void waitForPageLoad();

    void closeCurrentWindow();

    boolean verifyTitle(String title);

    String getCssValue(String elementName, String property, String attribute);

    String getExceptionDetails(Exception e);

    String setPageLoad(String pagName);

    void highlighElement(WebElement element);

    boolean switchContext(String mobileContext);

    void scrollToElement(String property, String elementName);

    boolean tap(String elementName, String property);

    void tap(int xAxis, int yAxis);

    boolean doubleTap(String elementName, String property);

    void doubleTap(int xAxis, int yAxis);

    boolean scroll(String fromElement, String toElement);

    void scroll(int fromXCordinate, int fromYCordinate, int toXCordinate, int toYCordinate);

    boolean zoom(String elementName, String property);

    boolean performMultiTouch(List<TouchAction> actions);

//    SeeTestClient getSeeTestClient();
//
//    void setSeeTestClient(SeeTestClient seetestClient);

}
