package com.github.arteam.jdit;

import org.skife.jdbi.v2.DBI;

import java.util.Properties;

/**
 * Date: 2/16/15
 * Time: 10:50 PM
 * Factory for creating DBI instances
 *
 * @author Artem Prigoda
 */
public interface DBIFactory {

    DBI createDBI(Properties properties);
}
