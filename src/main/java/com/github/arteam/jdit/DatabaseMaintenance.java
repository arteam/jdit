package com.github.arteam.jdit;

/**
 * Date: 1/1/16
 * Time: 6:57 PM
 * <p>
 * An interface for representing vendor-specific database
 * maintenance operations
 *
 * @author Artem Prigoda
 */
public interface DatabaseMaintenance {

    /**
     * Drop all tables and sequences
     */
    void dropTablesAndSequences();

    /**
     * Sweep data from DB, but don't drop the schema.
     * Also restart sequences, so tests can rely on their predictability
     */
    void sweepData();
}
