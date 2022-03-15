package com.xassure.framework.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;

import java.util.List;

public interface MobileControls extends Controls {
    AppiumDriver getDriver();

    boolean tap(String elementName, String property);

    void tap(int xAxis, int yAxis);

    boolean doubleTap(String elementName, String property);

    void doubleTap(int xAxis, int yAxis);

    boolean scroll(String fromElement, String toElement);

    void scroll(int fromXCordinate, int fromYCordinate, int toXCordinate, int toYCordinate);

    boolean zoom(String elementName, String property);

    boolean performMultiTouch(List<TouchAction> actions);

//    SeeTestClient getSeeTestClient();

//    void setSeeTestClient(SeeTestClient seetestClient);

}
