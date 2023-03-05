package com.github.arteam.jdit;

import org.jdbi.v3.core.Jdbi;

import java.util.Properties;

/**
 * A factory for creating a standard DBI instances from properties
 */
public class StandardDBIFactory implements DBIFactory {

    @Override
    public Jdbi createDBI(Properties properties) {
        Jdbi dbi = Jdbi.create(properties.getProperty("db.url"), properties.getProperty("db.username"),
                properties.getProperty("db.password"));
        dbi.installPlugins();
        return dbi;
    }
}
