package com.github.arteam.jdit.maintenance;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.statement.Batch;

/**
 * Date: 1/1/16
 * Time: 8:11 PM
 * Maintenance operations for the H2 database
 *
 * @author Artem Prigoda
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
            Batch batch = h.createBatch();
            batch.add("set referential_integrity false");

            ResultIterable<String> tables = h.createQuery("show tables")
                    .mapTo(String.class);
            for (String table : tables) {
                batch.add(String.format("truncate table \"%s\"", table));
            }

            ResultIterable<String> sequenceNames = h.createQuery("select sequence_name from information_schema.sequences")
                    .mapTo(String.class);
            for (String sequenceName : sequenceNames) {
                batch.add(String.format("alter sequence \"%s\" restart with 1", sequenceName));
            }
            batch.add("set referential_integrity true");
            batch.execute();
        });
    }
}
