package com.xassure.framework.webDriverFactory;

import org.openqa.selenium.WebDriver;

public interface IDriver {

    public WebDriver getDriver(String... params);
}
