package com.github.arteam.dropwizard.testing.jdbi;

import org.skife.jdbi.v2.Handle;

class DataMigration {

    private final Handle handle;

    DataMigration(Handle handle) {
        this.handle = handle;
    }

    public int[] executeScript(String scriptLocation) {
        return handle.createScript(scriptLocation).execute();
    }

    public void sweepData() {
        handle.execute("TRUNCATE SCHEMA public RESTART IDENTITY AND COMMIT");
    }
}
