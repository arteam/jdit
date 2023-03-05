package com.github.arteam.jdit.maintenance;

import org.jdbi.v3.core.Handle;

import java.sql.SQLException;

/**
 * A factory for creating vendor-specific {@link DatabaseMaintenance} instances
 * based on the current database vendor name
 */
public final class DatabaseMaintenanceFactory {

    private static final String POSTGRESQL = "PostgreSQL";
    private static final String HSQLDB = "HSQL Database Engine";
    private static final String H2 = "H2";
    private static final String MYSQL = "MySQL";

    private DatabaseMaintenanceFactory() {
    }

    public static DatabaseMaintenance create(Handle handle) {
        String databaseVendor = getDatabaseVendor(handle);
        return switch (databaseVendor) {
            case POSTGRESQL -> new PostgresDatabaseMaintenance(handle);
            case HSQLDB -> new HsqlDatabaseMaintenance(handle);
            case H2 -> new H2DatabaseMaintenance(handle);
            case MYSQL -> new MySqlDatabaseMaintenance(handle);
            default -> throw new UnsupportedOperationException(databaseVendor + " is not supported");
        };
    }

    private static String getDatabaseVendor(Handle handle) {
        try {
            return handle.getConnection().getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
