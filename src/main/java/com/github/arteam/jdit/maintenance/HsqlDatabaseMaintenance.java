package com.github.arteam.jdit.maintenance;


import org.jdbi.v3.core.Handle;

/**
 * Date: 1/1/16
 * Time: 7:40 PM
 * <p/>
 * Maintenance operations for the HSQLDB database
 *
 * @author Artem Prigoda
 */
class HsqlDatabaseMaintenance implements DatabaseMaintenance {

    private final Handle handle;

    HsqlDatabaseMaintenance(Handle handle) {
        this.handle = handle;
    }

    @Override
    public void dropTablesAndSequences() {
        // NOOP
    }

    @Override
    public void sweepData() {
        handle.execute("truncate schema public restart identity and commit");
    }
}
