package com.github.arteam.jdit;

import org.skife.jdbi.v2.*;

/**
 * Date: 1/1/16
 * Time: 7:05 PM
 * <p>
 * Maintenance operations against a PostgreSQL database
 *
 * @author Artem Prigoda
 */
public class PostgresDatabaseMaintenance implements DatabaseMaintenance {

    private Handle handle;

    public PostgresDatabaseMaintenance(Handle handle) {
        this.handle = handle;
    }

    public void sweepData() {
        handle.useTransaction(new TransactionConsumer() {
            @Override
            public void useTransaction(Handle h, TransactionStatus transactionStatus) throws Exception {
                String truncate = h.createQuery("select '" +
                        "truncate table ' " +
                        "|| string_agg(quote_ident(tablename), ', ') || " +
                        "' cascade' " +
                        "from  pg_tables " +
                        "where tableowner = (select current_user) " +
                        "and   schemaname = 'public'")
                        .mapTo(String.class)
                        .first();
                h.execute(truncate);
                Batch batch = h.createBatch();
                Query<String> sequenceNames = h.createQuery("select sequence_name from information_schema.sequences " +
                        "where sequence_schema='public' " +
                        "and sequence_catalog = (select current_catalog)")
                        .mapTo(String.class);
                for (String sequenceName : sequenceNames) {
                    batch.add(String.format("alter sequence \"%s\" restart with 1", sequenceName));
                }
                batch.execute();
            }
        });
    }

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
