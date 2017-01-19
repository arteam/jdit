package com.github.arteam.jdit;

import org.jdbi.v3.core.Jdbi;

import java.util.Properties;

/**
 * Date: 2/16/15
 * Time: 10:50 PM
 * Factory for creating DBI instances
 *
 * @author Artem Prigoda
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
