package com.github.arteam.jdit;

import org.skife.jdbi.v2.Handle;

import java.sql.SQLException;

/**
 * Date: 1/1/16
 * Time: 6:54 PM
 * <p/>
 * A factory for creating vendor-specific {@link DatabaseMaintenance} instances
 * based on the current database vendor name
 *
 * @author Artem Prigoda
 */
public final class DatabaseMaintenanceFactory {

    private static final String POSTGRESQL = "PostgreSQL";

    private DatabaseMaintenanceFactory() {
    }

    public static DatabaseMaintenance create(Handle handle) {
        String databaseVendor = getDatabaseVendor(handle);
        switch (databaseVendor) {
            case POSTGRESQL:
                return new PostgresDatabaseMaintenance(handle);
            case "HSQL Database Engine":
                return new HsqlDatabaseMaintenance(handle);
            default:
                throw new UnsupportedOperationException(databaseVendor + " is not supported");
        }
    }

    private static String getDatabaseVendor(Handle handle) {
        try {
            return handle.getConnection().getMetaData().getDatabaseProductName();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
