package com.github.arteam.jdit;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Date: 1/18/15
 * Time: 3:11 PM
 * <p/>
 * The current database context.
 * It's responsible for maintaining an active DB with a schema during the run of the tests.
 * <ul>
 * <li>The DB is lazily created at the first invocation</li>
 * <li>A {@link DBI} instance is created according to DB connection params</li>
 * <li>The instance is configured in similar way as in {@link io.dropwizard.jdbi.DBIFactory}
 * with some differences (SQL queries at INFO level, no metrics and health checks)
 * </li>
 * <li>The database schema is migrated (if enabled)</li>
 * </ul>
 *
 * @author Artem Prigoda
 */
public class DBIContext {

    private static final String USER_PROPERTIES_LOCATION = "/jdit.properties";
    private static final String DEFAULT_PROPERTIES_LOCATION = "/jdit-default.properties";
    private static final String DEFAULT_SCHEMA_LOCATION = "schema.sql";

    private static final Holder INSTANCE = new Holder();

    /**
     * Holder idiom for creating lazy singletons
     */
    private static class Holder {

        private DBI dbi;

        private Holder() {
            Properties properties = loadProperties();
            dbi = createDBI(properties);
            migrateSchema(properties);
        }

        /**
         * Load a properties file from the classpath
         *
         * @return DB configuration as {@link Properties}
         */
        private Properties loadProperties() {
            Properties properties = new Properties();
            Properties userProperties = new Properties();
            try (InputStream defaultStream = DBIContext.class.getResourceAsStream(DEFAULT_PROPERTIES_LOCATION);
                 InputStream userStream = DBIContext.class.getResourceAsStream(USER_PROPERTIES_LOCATION)) {
                if (defaultStream != null) {
                    properties.load(defaultStream);
                }
                if (userStream != null) {
                    userProperties.load(userStream);
                }
            } catch (IOException e) {
                throw new IllegalStateException("Unable load properties", e);
            }
            for (Map.Entry<Object, Object> entry : userProperties.entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
            if (properties.isEmpty()) {
                throw new IllegalStateException("No properties specified for JDBI Testing");
            }
            return properties;
        }

        /**
         * Create and configure a {@link DBI} instance from the properties
         * The DB is created during the first connection.
         *
         * @param properties configuration of DB
         * @return a new {@link DBI} instance for performing database access
         */
        private DBI createDBI(Properties properties) {
            try {
                DBIFactory dbiFactory = (DBIFactory) Class.forName(properties.getProperty("dbi.factory"))
                        .newInstance();
                return dbiFactory.createDBI(properties);
            } catch (Exception e) {
                throw new IllegalStateException("Unable instantiate DBI Factory", e);
            }
        }

        /**
         * Migrate the DB schema
         *
         * @param properties configuration of schema migration
         */
        public void migrateSchema(Properties properties) {
            String property = properties.getProperty("schema.migration.enabled");
            if (property == null || Boolean.parseBoolean(property)) {
                try (Handle handle = dbi.open()) {
                    String schemaLocation = properties.getProperty("schema.migration.location");
                    if (schemaLocation == null) {
                        schemaLocation = DEFAULT_SCHEMA_LOCATION;
                    }
                    new DataMigration(handle).executeScript(schemaLocation);
                }
            }
        }
    }

    /**
     * Get the current {@link DBI} instance
     *
     * @return configured {@link DBI} instance to an active DB
     */
    public static DBI getDBI() {
        return INSTANCE.dbi;
    }
}
