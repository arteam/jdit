package com.github.arteam.jdit;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Properties;

/**
 * A factory for creating a dynamic Postgres DBI instances
 */
public class EmbeddedPostgresDBIFactory implements DBIFactory {
    private static final String USER = "jdit";
    private static final String DATABASE = "jdit_test";
    private static final String PASSWORD = "test";
    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>("postgres:13.4");
        postgresContainer.start();
        try (Handle handle = Jdbi.create(postgresContainer.getJdbcUrl(), postgresContainer.getUsername(),
                postgresContainer.getPassword()).open()) {
            handle.execute(String.format("create user %s with password '%s'", USER, PASSWORD));
            handle.execute(String.format("create database %s with owner %s", DATABASE, USER));
        }
        Runtime.getRuntime().addShutdownHook(new Thread(postgresContainer::stop));
    }

    @Override
    public Jdbi createDBI(Properties properties) {
        Jdbi dbi = Jdbi.create(String.format("jdbc:postgresql://127.0.0.1:%d/%s",
                postgresContainer.getMappedPort(5432), DATABASE), USER, PASSWORD);
        dbi.installPlugins();
        return dbi;
    }
}
