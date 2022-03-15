package com.xassure.framework.driver;

import com.google.inject.Inject;
import com.xassure.framework.annotations.Mobile;
import com.xassure.framework.annotations.Web;

public class Mob_CloseDriver {

    Controls controls;

    @Inject
    public Mob_CloseDriver(@Mobile Controls controls) {
        this.controls = controls;
    }

    public void quitDriver() {

        controls.quitDriver();
    }
}
