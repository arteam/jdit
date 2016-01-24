package com.github.arteam.jdit;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.logging.PrintStreamLog;

import java.util.Properties;

/**
 * Date: 2/17/15
 * Time: 10:29 PM
 * <p>
 * Factory for creating a standard DBI instance from properties
 * <p>
 * A single addition is that SQL queries are logged to the console.
 *
 * @author Artem Prigoda
 */
public class StandardDBIFactory implements DBIFactory {

    @Override
    public DBI createDBI(Properties properties) {
        DBI dbi = new DBI(properties.getProperty("db.url"), properties.getProperty("db.username"),
                properties.getProperty("db.password"));
        dbi.setSQLLog(new PrintStreamLog());
        return dbi;
    }
}
