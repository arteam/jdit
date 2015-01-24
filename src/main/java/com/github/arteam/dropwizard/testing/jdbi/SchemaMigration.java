package com.github.arteam.dropwizard.testing.jdbi;

import org.skife.jdbi.v2.Handle;

class SchemaMigration {

    private static final String SCHEMA_LOCATION = "schema.sql";

    public int[] migrate(Handle handle) {
        return handle.createScript(SCHEMA_LOCATION).execute();
    }
}
