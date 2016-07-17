package com.github.arteam.jdit.maintenance;

import org.skife.jdbi.v2.*;

/**
 * Date: 1/1/16
 * Time: 7:05 PM
 * <p/>
 * Maintenance operations for the PostgreSQL database
 *
 * @author Artem Prigoda
 */
class PostgresDatabaseMaintenance implements DatabaseMaintenance {

    private Handle handle;

    public PostgresDatabaseMaintenance(Handle handle) {
        this.handle = handle;
    }

    public void sweepData() {
        handle.useTransaction((h, transactionStatus) -> {
            Batch batch = h.createBatch();
            Query<String> tableForeignKeys = h.createQuery(
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
            Query<String> tableNames = h.createQuery("select tablename from pg_tables " +
                    "where tableowner = (select current_user) " +
                    "and schemaname = 'public'")
                    .mapTo(String.class);
            for (String tableName : tableNames) {
                batch.add(String.format("delete from \"%s\"", tableName));
            }
            Query<String> sequenceNames = h.createQuery("select sequence_name from information_schema.sequences " +
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
        handle.useTransaction((h, transactionStatus) -> {
            String currentUser = h.createQuery("select current_user")
                    .mapTo(String.class)
                    .first();
            h.execute(String.format("drop owned by \"%s\"", currentUser));
        });
    }
}
