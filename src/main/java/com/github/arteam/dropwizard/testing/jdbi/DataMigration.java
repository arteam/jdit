package com.github.arteam.dropwizard.testing.jdbi;

import org.skife.jdbi.v2.Handle;

class DataMigration {

    private static final String SCHEMA_LOCATION = "schema.sql";

    public int[] migrateSchema(Handle handle) {
        return executeScript(handle, SCHEMA_LOCATION);
    }

    public int[] executeScript(Handle handle, String scriptLocation){
        return handle.createScript(scriptLocation).execute();
    }
}
