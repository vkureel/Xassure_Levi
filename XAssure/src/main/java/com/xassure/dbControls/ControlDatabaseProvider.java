package com.xassure.dbControls;

import com.google.inject.Provider;
import com.xassure.dbControls.dbUtils.GetSessionFactory;
import org.hibernate.Session;

public class ControlDatabaseProvider implements Provider<DatabaseControls> {

    private static Session session;

    @Override
    public DatabaseControls get() {
        session = GetSessionFactory.getSessionFactory().openSession();
        return new DatabaseControlLibrary(session);

    }

}
