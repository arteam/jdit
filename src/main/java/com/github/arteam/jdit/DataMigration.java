package com.github.arteam.jdit;

import org.skife.jdbi.v2.Handle;

/**
 * A component that responsible for migrating data in the DB
 */
class DataMigration {

    private final Handle handle;

    DataMigration(Handle handle) {
        this.handle = handle;
    }

    /**
     * Execute a script a from a classpath location
     *
     * @param scriptLocation script location (without leading slash).
     *                       If one exists, it's trimmed
     */
    public void executeScript(String scriptLocation) {
        String correctLocation = !scriptLocation.startsWith("/") ?
                scriptLocation : scriptLocation.substring(1);
        handle.createScript(correctLocation).executeAsSeparateStatements();
    }

    /**
     * Sweep data from DB, but don't drop the schema.
     * Also restart sequences, so tests can rely on its predictability
     */
    public void sweepData() {
        handle.execute("TRUNCATE SCHEMA public RESTART IDENTITY AND COMMIT");
    }
}
