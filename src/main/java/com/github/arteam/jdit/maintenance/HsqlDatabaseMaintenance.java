package com.github.arteam.jdit.maintenance;

import org.skife.jdbi.v2.Handle;

/**
 * Date: 1/1/16
 * Time: 7:40 PM
 * <p/>
 * Maintenance operations against HSQLB
 *
 * @author Artem Prigoda
 */
class HsqlDatabaseMaintenance implements DatabaseMaintenance {

    private final Handle handle;

    public HsqlDatabaseMaintenance(Handle handle) {
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
