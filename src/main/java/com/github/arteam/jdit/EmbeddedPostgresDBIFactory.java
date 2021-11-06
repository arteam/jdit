package com.github.arteam.jdit;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.io.IOException;
import java.util.Properties;

/**
 * Factory for creating a dynamic Postgres DBI instance
 */
public class EmbeddedPostgresDBIFactory implements DBIFactory {

    private static final String USER = "jdit";
    private static final String DATABASE = "jdit_test";
    private static final String PASSWORD = "test";
    private static final EmbeddedPostgres embeddedPostgres;

    static {
        try {
            embeddedPostgres = EmbeddedPostgres.builder().start();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        try (Handle handle = Jdbi.create(embeddedPostgres.getPostgresDatabase()).open();) {
            handle.execute(String.format("create user %s with password '%s'", USER, PASSWORD));
            handle.execute(String.format("create database %s with owner %s", DATABASE, USER));
        }
    }

    @Override
    public Jdbi createDBI(Properties properties) {
        Jdbi dbi = Jdbi.create(String.format("jdbc:postgresql://127.0.0.1:%d/%s", embeddedPostgres.getPort(), DATABASE),
                USER, PASSWORD);
        dbi.installPlugins();
        return dbi;
    }
}
