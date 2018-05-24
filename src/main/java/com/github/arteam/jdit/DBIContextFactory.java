package com.github.arteam.jdit;

import org.jdbi.v3.core.Jdbi;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating instances of {@link DBIContext} by properties files,
 * specified by the user.
 */
public class DBIContextFactory {

    private static final Map<String, Jdbi> contexts = new HashMap<>();
    private static final String DEFAULT_PROPERTIES_LOCATION = "jdit.properties";

    /**
     * Get the default {@link Jdbi} instance
     *
     * @return configured {@link Jdbi} instance to an active DB
     */
    public static Jdbi getDBI() {
        return getDBI(DEFAULT_PROPERTIES_LOCATION);
    }

    static Jdbi getDBI(String propertiesLocation) {
        return contexts.computeIfAbsent(propertiesLocation, p -> DBIContext.create(propertiesLocation));
    }
}
