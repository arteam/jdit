package com.github.arteam.dropwizard.testing.jdbi;

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
 *
 * @author Artem Prigoda
 */
public class DBIContext {

    public static final Logger LOG = (Logger) LoggerFactory.getLogger(DBI.class);
    private static final String PROPERTIES_LOCATION = "/jdbi-testing.properties";
    private static DBI dbi;

    private DBIContext() {
    }

    private static DBI createDBI() {
        Properties properties = new Properties();
        try (InputStream stream = DBIContext.class.getResourceAsStream(PROPERTIES_LOCATION)) {
            properties.load(stream);
        } catch (IOException e) {
            throw new IllegalStateException("Unable load properties from " + PROPERTIES_LOCATION, e);
        }

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

    public static synchronized DBI getDBI() {
        if (dbi == null) {
            dbi = createDBI();
            try (Handle h = dbi.open()) {
                new DataMigration(h).migrateSchema();
            }
        }
        return dbi;
    }


}

