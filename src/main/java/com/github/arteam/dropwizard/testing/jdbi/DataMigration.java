package com.github.arteam.dropwizard.testing.jdbi;

import org.skife.jdbi.v2.Handle;

class DataMigration {

    private static final String SCHEMA_LOCATION = "schema.sql";

    private final Handle handle;

    DataMigration(Handle handle) {
        this.handle = handle;
    }

    public int[] migrateSchema() {
        return executeScript(SCHEMA_LOCATION);
    }

    public int[] executeScript(String scriptLocation) {
        return handle.createScript(scriptLocation).execute();
    }

    public void sweepData() {
        handle.execute("TRUNCATE SCHEMA public AND COMMIT");
    }
}
