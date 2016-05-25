package com.github.arteam.jdit.maintenance;

import org.skife.jdbi.v2.*;

/**
 * Date: 2/1/16
 * Time: 8:01 PM
 * <p/>
 * Maintenance operations for the MySQL database
 *
 * @author Artem Prigoda
 */
class MySqlDatabaseMaintenance implements DatabaseMaintenance {

    private Handle handle;

    public MySqlDatabaseMaintenance(Handle handle) {
        this.handle = handle;
    }

    public void sweepData() {
        performForEveryTable("truncate");
    }

    public void dropTablesAndSequences() {
        performForEveryTable("drop");
    }

    private void performForEveryTable(final String operation) {
        handle.useTransaction((h, status) -> {
            Batch batch = h.createBatch();
            batch.add("set foreign_key_checks=0");
            Query<String> tableNames = h.createQuery("select table_name from information_schema.tables " +
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
