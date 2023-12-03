package com.github.arteam.jdit;

import com.github.arteam.jdit.maintenance.DatabaseMaintenance;
import com.github.arteam.jdit.maintenance.DatabaseMaintenanceFactory;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Properties;

/**
 * The current database context.
 * <p>It's responsible for maintaining an active DB with a schema during the run of the tests</p>
 * <ul>
 * <li>The DB is lazily created at the first invocation</li>
 * <li>A {@link Jdbi} instance is created according to DB connection params</li>
 * <li>The instance is configured in the similar way as in Dropwizard
 * with some differences (SQL queries at INFO level, no metrics and health checks)
 * </li>
 * <li>The database schema is migrated (if enabled)</li>
 * </ul>
 */
public class DBIContext {

    private static final Logger log = LoggerFactory.getLogger(DBIContextFactory.class);

    private static final String DEFAULT_PROPERTIES_LOCATION = "jdit-default.properties";
    private static final String DEFAULT_SCHEMA_LOCATION = "schema.sql";

    private final Jdbi dbi;

    private final Comparator<String> migrationFileComparator;

    private DBIContext(String propertiesLocation) {
        Properties properties = loadProperties(propertiesLocation);
        dbi = createDBI(properties);
        migrationFileComparator = createFilesComparator(properties);
        migrateSchema(properties);
    }

    public static Jdbi create(String propertiesLocation) {
        return new DBIContext(propertiesLocation).dbi;
    }

    /**
     * Load a properties file from the classpath and merges it with the default properties
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
        properties.putAll(userProperties);
        if (properties.isEmpty()) {
            throw new IllegalStateException("No properties specified for JDBI Testing");
        }
        return properties;
    }

    /**
     * Create and configure a {@link Jdbi} instance from the properties
     * The DB is created during the first connection.
     *
     * @param properties configuration of DB
     * @return a new {@link Jdbi} instance for performing database access
     */
    private Jdbi createDBI(Properties properties) {
        try {
            DBIFactory dbiFactory = (DBIFactory) Class.forName(properties.getProperty("dbi.factory"))
                    .getConstructor().newInstance();
            return dbiFactory.createDBI(properties);
        } catch (Exception e) {
            throw new IllegalStateException("Unable instantiate DBI Factory", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Comparator<String> createFilesComparator(Properties properties) {
        try {
            if (!properties.containsKey("schema.migration.file.comparator")) {
                return null;
            }
            return (Comparator<String>) Class.forName(properties.getProperty("schema.migration.file.comparator"))
                    .getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create a comparator", e);
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
            String schemaLocation = Objects.requireNonNullElse(properties.getProperty("schema.migration.location"), DEFAULT_SCHEMA_LOCATION);
            DatabaseMaintenance databaseMaintenance = DatabaseMaintenanceFactory.create(handle);
            databaseMaintenance.dropTablesAndSequences();

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
            log.warn("Directory '{}' is empty. Migrations are not applied", directory);
            return;
        }
        if (migrationFileComparator != null) {
            Arrays.sort(childFileNames, migrationFileComparator);
        } else {
            Arrays.sort(childFileNames);
        }
        for (String childFileName : childFileNames) {
            String childFileLocation = schemaLocation + File.separator + childFileName;
            if (!childFileName.endsWith("sql")) {
                log.warn("'{}' is not an SQL script. It's ignored", childFileLocation);
                continue;
            }
            dataMigration.executeScript(childFileLocation);
        }
    }
}
