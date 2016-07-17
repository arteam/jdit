package com.github.arteam.jdit;

import org.skife.jdbi.v2.*;

/**
 * Component which is responsible for migrating data in the DB
 */
class DataMigration {

    private final Handle handle;

    public DataMigration(Handle handle) {
        this.handle = handle;
    }

    /**
     * Executes a script a from a classpath location
     *
     * @param scriptLocation script location (without the leading slash).
     *                       If one exists, it's trimmed.
     */
    public void executeScript(String scriptLocation) {
        String correctLocation = !scriptLocation.startsWith("/") ?
                scriptLocation : scriptLocation.substring(1);
        handle.createScript(correctLocation).executeAsSeparateStatements();
    }
}
