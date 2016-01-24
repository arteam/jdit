package com.github.arteam.jdit;

import org.skife.jdbi.v2.DBI;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 1/18/15
 * Time: 3:11 PM
 * <p>
 * Factory for creating instances of {@link DBIContext} by properties files,
 * specified by the user.
 *
 * @author Artem Prigoda
 */
public class DBIContextFactory {

    private static final Map<String, DBI> CONTEXTS = new HashMap<>();
    private static final String DEFAULT_PROPERTIES_LOCATION = "jdit.properties";

    /**
     * Get the default {@link DBI} instance
     *
     * @return configured {@link DBI} instance to an active DB
     */
    public static DBI getDBI() {
        return getDBI(DEFAULT_PROPERTIES_LOCATION);
    }

    public static DBI getDBI(String propertiesLocation) {
        DBI dbi = CONTEXTS.get(propertiesLocation);
        if (dbi == null) {
            CONTEXTS.put(propertiesLocation, dbi = DBIContext.create(propertiesLocation));
        }
        return dbi;
    }
}
