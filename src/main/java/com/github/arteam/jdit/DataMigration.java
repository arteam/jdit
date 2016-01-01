package com.github.arteam.jdit;

import org.skife.jdbi.v2.*;

/**
 * Component that's responsible for migrating data in the DB
 */
class DataMigration {

    private final Handle handle;

    public DataMigration(Handle handle) {
        this.handle = handle;
    }

    /**
     * Execute a script a from a classpath location
     *
     * @param scriptLocation script location (without leading slash).
     *                       If one exists, it's trimmed
     */
    public void executeScript(String scriptLocation) {
        String correctLocation = !scriptLocation.startsWith("/") ?
                scriptLocation : scriptLocation.substring(1);
        handle.createScript(correctLocation).executeAsSeparateStatements();
    }

    /**
     * Sweep data from DB, but don't drop the schema.
     * Also restart sequences, so tests can rely on their predictability
     */
    public void sweepData() {
        handle.useTransaction(new TransactionConsumer() {
            @Override
            public void useTransaction(Handle h, TransactionStatus transactionStatus) throws Exception {
                Query<String> tableNames = h.createQuery("select tablename from pg_tables " +
                        "where tableowner = (select current_user) " +
                        "and schemaname = 'public'")
                        .mapTo(String.class);
                Batch batch = h.createBatch();
                for (String tableName : tableNames) {
                    batch.add(String.format("truncate table \"%s\" cascade", tableName));
                }
                Query<String> sequenceNames = h.createQuery("select sequence_name from information_schema.sequences " +
                        "where sequence_schema='public' " +
                        "and sequence_catalog = (select current_catalog)")
                        .mapTo(String.class);
                for (String sequenceName : sequenceNames) {
                    h.execute(String.format("alter sequence \"%s\" restart with 1", sequenceName));
                }
                batch.execute();
            }
        });
    }

    /**
     * Drop all tables and sequences
     */
    public void dropTablesAndSequences() {
        handle.useTransaction(new TransactionConsumer() {
            @Override
            public void useTransaction(Handle h, TransactionStatus transactionStatus) throws Exception {
                String currentUser = h.createQuery("select current_user")
                        .mapTo(String.class)
                        .first();
                h.execute(String.format("drop owned by \"%s\"", currentUser));
            }
        });
    }
}
