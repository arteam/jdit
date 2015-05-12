package com.github.arteam.jdit;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Date: 2/22/15
 * Time: 10:06 PM
 * <p/>
 * The current database context.
 * <p>It's responsible for maintaining an active DB with a schema during the run of the tests</p>
 * <ul>
 * <li>The DB is lazily created at the first invocation</li>
 * <li>A {@link DBI} instance is created according to DB connection params</li>
 * <li>The instance is configured in the similar way as in {@link io.dropwizard.jdbi.DBIFactory}
 * with some differences (SQL queries at INFO level, no metrics and health checks)
 * </li>
 * <li>The database schema is migrated (if enabled)</li>
 * </ul>
 *
 * @author Artem Prigoda
 */
public class DBIContext {

    private static final Logger log = LoggerFactory.getLogger(DBIContextFactory.class);

    private static final String DEFAULT_PROPERTIES_LOCATION = "jdit-default.properties";
    private static final String DEFAULT_SCHEMA_LOCATION = "schema.sql";

    private DBI dbi;

    private DBIContext(String propertiesLocation) {
        Properties properties = loadProperties(propertiesLocation);
        dbi = createDBI(properties);
        migrateSchema(properties);
    }

    public static DBI create(String propertiesLocation) {
        return new DBIContext(propertiesLocation).dbi;
    }

    /**
     * Load a properties file from the classpath
     *
     * @return DB configuration as {@link Properties}
     */
    private Properties loadProperties(String userPropertiesLocations) {
        Properties properties = new Properties();
        Properties userProperties = new Properties();
        try (InputStream defaultStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_LOCATION);
             InputStream userStream = getClass().getClassLoader().getResourceAsStream(userPropertiesLocations)) {
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
    private void migrateSchema(Properties properties) {
        String property = properties.getProperty("schema.migration.enabled");
        if (property != null && !Boolean.parseBoolean(property)) {
            return;
        }
        try (Handle handle = dbi.open()) {
            String schemaLocation = properties.getProperty("schema.migration.location");
            if (schemaLocation == null) {
                schemaLocation = DEFAULT_SCHEMA_LOCATION;
            }
            DataMigration dataMigration = new DataMigration(handle);

            URL resource = getClass().getClassLoader().getResource(schemaLocation);
            if (resource == null) {
                throw new IllegalArgumentException("File '" + schemaLocation + " is not exist in resources");
            }
            File file = new File(resource.getFile());
            if (file.isFile()) {
                dataMigration.executeScript(schemaLocation);
            } else {
                migrateDirectory(dataMigration, file, schemaLocation);
            }
        }
    }

    private void migrateDirectory(DataMigration dataMigration, File directory, String schemaLocation) {
        String[] childFileNames = directory.list();
        if (childFileNames == null || childFileNames.length == 0) {
            log.warn("Directory '" + directory + "' is empty. Migrations are not applied");
            return;
        }
        Arrays.sort(childFileNames);
        for (String childFileName : childFileNames) {
            String childFileLocation = schemaLocation + File.separator + childFileName;
            if (!childFileName.endsWith("sql")) {
                log.warn("'" + childFileLocation + "' is not an SQL script. It's ignored");
                continue;
            }
            dataMigration.executeScript(childFileLocation);
        }
    }
}
