package com.github.arteam.jdit;

import org.jdbi.v3.core.Jdbi;

import java.util.Properties;

/**
 * Factory for creating DBI instances
 */
public interface DBIFactory {

    /**
     * Create a custom DBI instance
     *
     * @param properties database properties (url, username, password)
     * @return a configured DBI instance
     */
    Jdbi createDBI(Properties properties);
}
