package com.github.arteam.dropwizard.testing.jdbi;

import org.skife.jdbi.v2.Handle;

class SchemaMigration {

    private static final String SCHEMA_LOCATION = "schema.sql";

    public void migrate(Handle handle) {
        handle.createScript(SCHEMA_LOCATION).execute();
    }
}
