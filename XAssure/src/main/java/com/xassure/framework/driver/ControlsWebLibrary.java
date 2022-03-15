package com.xassure.framework.driver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.xassure.framework.webDriverFactory.DriverBinding;

public class ControlsWebLibrary {

    public static void main(String[] args) {

        Injector injector = Guice.createInjector(new DriverBinding());
        WebLibrary webLibrary = injector.getInstance(WebLibrary.class);
        webLibrary.setDriver("CHROME", "false");
        webLibrary.launchApplication("https://www.google.com");
        webLibrary.quitDriver();
    }
}
