package com.github.arteam.jdit.maintenance;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.result.ResultIterable;
import org.jdbi.v3.core.statement.Batch;

/**
 * Maintenance operations for the PostgreSQL database
 */
class PostgresDatabaseMaintenance implements DatabaseMaintenance {

    private Handle handle;

    PostgresDatabaseMaintenance(Handle handle) {
        this.handle = handle;
    }

    public void sweepData() {
        handle.useTransaction(h -> {
            Batch batch = h.createBatch();
            ResultIterable<String> tableForeignKeys = h.createQuery(
                    "select 'alter table \"' || relname || '\" drop constraint \"'|| conname ||'\"' " +
                    "from pg_constraint " +
                    "inner join pg_class on conrelid=pg_class.oid " +
                    "inner join pg_namespace on pg_namespace.oid=pg_class.relnamespace " +
                    "where pg_constraint.contype = 'f' " +
                    "order by nspname, relname, conname")
                    .mapTo(String.class);
            for (String alterTable : tableForeignKeys) {
                batch.add(alterTable);
            }
            ResultIterable<String> tableNames = h.createQuery("select tablename from pg_tables " +
                    "where tableowner = (select current_user) " +
                    "and schemaname = 'public'")
                    .mapTo(String.class);
            for (String tableName : tableNames) {
                batch.add(String.format("delete from \"%s\"", tableName));
            }
            ResultIterable<String> sequenceNames = h.createQuery("select sequence_name from information_schema.sequences " +
                    "where sequence_schema='public' " +
                    "and sequence_catalog = (select current_catalog)")
                    .mapTo(String.class);
            for (String sequenceName : sequenceNames) {
                batch.add(String.format("alter sequence \"%s\" restart with 1", sequenceName));
            }
            batch.execute();
        });
    }

    public void dropTablesAndSequences() {
        handle.useTransaction(h -> {
            String currentUser = h.createQuery("select current_user")
                    .mapTo(String.class)
                    .findOnly();
            h.execute(String.format("drop owned by \"%s\"", currentUser));
        });
    }
}
