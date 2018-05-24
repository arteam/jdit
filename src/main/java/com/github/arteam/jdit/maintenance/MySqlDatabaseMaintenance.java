package com.github.arteam.jdit.maintenance;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.statement.Batch;

/**
 * Maintenance operations for the MySQL database
 */
class MySqlDatabaseMaintenance implements DatabaseMaintenance {

    private Handle handle;

    MySqlDatabaseMaintenance(Handle handle) {
        this.handle = handle;
    }

    public void sweepData() {
        performForEveryTable("truncate");
    }

    public void dropTablesAndSequences() {
        performForEveryTable("drop");
    }

    private void performForEveryTable(final String operation) {
        handle.useTransaction(h -> {
            Batch batch = h.createBatch();
            batch.add("set foreign_key_checks=0");
            ResultIterable<String> tableNames = h.createQuery("select table_name from information_schema.tables " +
                    "where table_schema = (select database())")
                    .mapTo(String.class);
            for (String tableName : tableNames) {
                batch.add(String.format("%s table `%s`", operation, tableName));
            }
            batch.add("set foreign_key_checks=1");
            batch.execute();
        });
    }
}
