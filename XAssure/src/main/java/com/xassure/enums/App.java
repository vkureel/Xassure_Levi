package com.xassure.enums;

public enum App {

    //    LOCALE_DATAPROVIDER("./src/main/resources/dataprovider/multiLocale.json"),
    LOCALE_DATAPROVIDER("testConfig/dataprovider"),
    CHROMDRIVER_MAC("src/main/resources/drivers/chromedriver"),
    CHROMDRIVER_LINUX("src/main/resources/drivers/chromedriverlinux"),
    CHROMEDRIVER_WINDOWS("src/main/resources/drivers/chromedriver.exe"),
    GECKODRIVER_MAC("src/main/resources/drivers/geckodriver"),
    GECKODRIVER_LINUX("src/main/resources/drivers/geckodriverlinux"),
    GECKODRIVER_WINDOWS("src/main/resources/drivers/geckodriver.exe"),
    IEDRIVER_WINDOWS("src/main/resources/drivers/iedriver.exe"),
    EDGE_WINDOWS("src/main/resources/drivers/MicrosoftWebDriver.exe"),
    EDGE_LINUX("src/main/resources/drivers/MicrosoftWebDriverlinux"),
    BROWSERCONFIGURATIONPATH("src/main/resources/setup/browserstackConfig/browserstackConfiguration.json"),
    EXPERITESTCONFIGURATION("src/main/resources/setup/experitestConfig/experitestConfiguration.json");

    public final String property;

    App(String property) {
        this.property = property;
    }
}
