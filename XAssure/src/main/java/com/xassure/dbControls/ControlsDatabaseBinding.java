package com.xassure.dbControls;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ControlsDatabaseBinding extends AbstractModule {

    @Override
    protected void configure() {
        bind(DatabaseControls.class).annotatedWith(Names.named("db")).toProvider(ControlDatabaseProvider.class);

    }

}
