package com.github.arteam.jdit;

import org.jdbi.v3.core.Jdbi;
import org.testcontainers.containers.MySQLContainer;

import java.util.Properties;

/**
 * Factory for creating embedded MySQL DBI instances
 */
public class EmbeddedMysqlDBIFactory implements DBIFactory {

    private static final MySQLContainer<?> mysqlContainer;

    static {
        mysqlContainer = new MySQLContainer<>("mysql:8.0");
        mysqlContainer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(mysqlContainer::stop));
    }

    @Override
    public Jdbi createDBI(Properties properties) {
        Jdbi dbi = Jdbi.create(mysqlContainer.getJdbcUrl(), mysqlContainer.getUsername(), mysqlContainer.getPassword());
        dbi.installPlugins();
        return dbi;
    }
}
