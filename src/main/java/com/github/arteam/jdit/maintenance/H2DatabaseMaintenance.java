package com.github.arteam.jdit.maintenance;

import org.skife.jdbi.v2.*;

/**
 * Date: 1/1/16
 * Time: 8:11 PM
 * Maintenance operations for the H2 database
 *
 * @author Artem Prigoda
 */
class H2DatabaseMaintenance implements DatabaseMaintenance {

    private final Handle handle;

    public H2DatabaseMaintenance(Handle handle) {
        this.handle = handle;
    }

    @Override
    public void dropTablesAndSequences() {
        // NOOP
    }

    @Override
    public void sweepData() {
        handle.useTransaction((h, status) -> {
            Batch batch = h.createBatch();
            batch.add("set referential_integrity false");

            Query<String> tables = h.createQuery("show tables")
                    .mapTo(String.class);
            for (String table : tables) {
                batch.add(String.format("truncate table \"%s\"", table));
            }

            Query<String> sequenceNames = h.createQuery("select sequence_name from information_schema.sequences")
                    .mapTo(String.class);
            for (String sequenceName : sequenceNames) {
                batch.add(String.format("alter sequence \"%s\" restart with 1", sequenceName));
            }
            batch.add("set referential_integrity true");
            batch.execute();
        });
    }
}
