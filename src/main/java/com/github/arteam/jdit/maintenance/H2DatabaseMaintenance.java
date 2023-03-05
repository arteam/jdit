package com.github.arteam.jdit.maintenance;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.Batch;

/**
 * Maintenance operations for the H2 database
 */
class H2DatabaseMaintenance implements DatabaseMaintenance {

    private final Handle handle;

    H2DatabaseMaintenance(Handle handle) {
        this.handle = handle;
    }

    @Override
    public void dropTablesAndSequences() {
        // NOOP
    }

    @Override
    public void sweepData() {
        handle.useTransaction(h -> {
            Batch batch = h.createBatch()
                    .add("set referential_integrity false");
            h.createQuery("show tables")
                    .mapTo(String.class)
                    .forEach(table -> batch.add(String.format("truncate table \"%s\"", table)));
            h.createQuery("select sequence_name from information_schema.sequences")
                    .mapTo(String.class)
                    .forEach(s -> batch.add(String.format("alter sequence \"%s\" restart with 1", s)));
            batch.add("set referential_integrity true");
            batch.execute();
        });
    }
}
