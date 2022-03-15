package com.xassure.framework.driver;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ApiBinding extends AbstractModule {

    @Override
    protected void configure() {
        bind(RestControls.class).annotatedWith(Names.named("api")).toProvider(ControlsApiProvider.class);
    }

}
