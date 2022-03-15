package com.xassure.enums;

public enum Drivers {

    CHROME("chrome"), SAFARI("safari"), FIREFOX("firefox"),IE("IE"),EDGE("EDGE"), BROWSERSTACK("BR_STACK"),EXPERITEST("EX_TEST");

    final public String driver;

    Drivers(String driver) {
        this.driver = driver;
    }
}
