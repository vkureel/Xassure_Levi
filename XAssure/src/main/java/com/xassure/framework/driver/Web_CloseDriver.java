package com.xassure.framework.driver;

import com.google.inject.Inject;
import com.xassure.framework.annotations.Web;

public class Web_CloseDriver {

    Controls controls;

    @Inject
    public Web_CloseDriver(@Web Controls controls) {
        this.controls = controls;
    }

    public void quitDriver() {

        controls.quitDriver();
    }
}
