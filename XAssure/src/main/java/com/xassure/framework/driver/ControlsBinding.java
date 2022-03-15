package com.xassure.framework.driver;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.xassure.framework.annotations.Mobile;
import com.xassure.framework.annotations.Web;

public class ControlsBinding extends AbstractModule {


    @Override
    protected void configure() {
        binder()
                .bind(Controls.class)
                .annotatedWith(Web.class)
                .toProvider(ControlsWebProvider.class)
                .in(Scopes.NO_SCOPE);


        binder().
                bind(Controls.class).
                annotatedWith(Mobile.class).
                toProvider(ControlsMobileProvider.class);
    }

    @Provides
    public ApiControlsLibrary apiControlsLibrary() {
        return new ApiControlsLibrary();
    }
}