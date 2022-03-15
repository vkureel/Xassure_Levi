package com.xassure.framework.webDriverFactory;

import com.google.inject.name.Named;
import com.xassure.framework.driver.Controls;

public class WebPageDriver {
    Controls controls;

    public WebPageDriver(@Named("web") Controls controls) {
        this.controls = controls;
    }
}
