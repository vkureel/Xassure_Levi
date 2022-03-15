package com.xassure.framework.driver;


import com.google.inject.Provider;

public class ControlsApiProvider implements Provider<RestControls> {

    @Override
    public RestControls get() {

        return new ApiControlsLibrary();
    }

}
