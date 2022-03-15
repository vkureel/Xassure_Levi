package com.xassure.framework.webDriverFactory;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;

public class DriverBinding extends AbstractModule {

    @Override
    public void configure() {
        MapBinder<String, IDriver> myBinder =
                MapBinder.newMapBinder(binder(), String.class, IDriver.class);
        myBinder.addBinding("CHROME").to(CH_Driver.class);
        myBinder.addBinding("SAFARI").to(Safari_Driver.class);
        myBinder.addBinding("FIREFOX").to(FF_Driver.class);
        myBinder.addBinding("IE").to(IE_Driver.class);
        myBinder.addBinding("EDGE").to(Edge_Driver.class);
        myBinder.addBinding("BR_STACK").to(BrowserStackDriver.class);
        myBinder.addBinding("EX_TEST").to(ExperitestDriver.class);
    }
}
