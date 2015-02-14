package com.github.arteam.jdit;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.dropwizard.jdbi.ImmutableListContainerFactory;
import io.dropwizard.jdbi.ImmutableSetContainerFactory;
import io.dropwizard.jdbi.NamePrependingStatementRewriter;
import io.dropwizard.jdbi.OptionalContainerFactory;
import io.dropwizard.jdbi.args.JodaDateTimeArgumentFactory;
import io.dropwizard.jdbi.args.JodaDateTimeMapper;
import io.dropwizard.jdbi.args.OptionalArgumentFactory;
import io.dropwizard.jdbi.logging.LogbackLog;
import org.skife.jdbi.v2.ColonPrefixNamedParamStatementRewriter;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
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

    private static final String PROPERTIES_LOCATION = "/jdbi-testing.properties";
    private static final String DEFAULT_SCHEMA_LOCATION = "schema.sql";

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(DBI.class);
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
            try (InputStream stream = DBIContext.class.getResourceAsStream(PROPERTIES_LOCATION)) {
                properties.load(stream);
            } catch (IOException e) {
                throw new IllegalStateException("Unable load properties from " + PROPERTIES_LOCATION, e);
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
            DBI dbi = new DBI(properties.getProperty("db.url"), properties.getProperty("db.username"),
                    properties.getProperty("db.password"));

            dbi.setSQLLog(new LogbackLog(LOG, Level.INFO));
            dbi.setStatementRewriter(new NamePrependingStatementRewriter(new ColonPrefixNamedParamStatementRewriter()));
            dbi.registerArgumentFactory(new OptionalArgumentFactory(null));
            dbi.registerContainerFactory(new ImmutableListContainerFactory());
            dbi.registerContainerFactory(new ImmutableSetContainerFactory());
            dbi.registerContainerFactory(new OptionalContainerFactory());
            dbi.registerArgumentFactory(new JodaDateTimeArgumentFactory());
            dbi.registerMapper(new JodaDateTimeMapper());
            return dbi;
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
