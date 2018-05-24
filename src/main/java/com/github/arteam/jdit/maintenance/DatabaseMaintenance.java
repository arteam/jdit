package com.github.arteam.jdit.maintenance;

/**
 * An interface for representing vendor-specific database maintenance operations
 */
public interface DatabaseMaintenance {

    /**
     * Drop all tables and sequences
     */
    void dropTablesAndSequences();

    /**
     * Sweep data from the DB, but don't drop the schema.
     * Also restart sequences, so tests can rely on their predictability.
     */
    void sweepData();
}
