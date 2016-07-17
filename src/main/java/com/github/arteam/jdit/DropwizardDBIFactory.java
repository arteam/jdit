package com.github.arteam.jdit;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.dropwizard.jdbi.*;
import io.dropwizard.jdbi.args.*;
import io.dropwizard.jdbi.logging.LogbackLog;
import org.skife.jdbi.v2.ColonPrefixNamedParamStatementRewriter;
import org.skife.jdbi.v2.DBI;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Properties;
import java.util.TimeZone;

/**
 * Date: 2/16/15
 * Time: 10:50 PM
 * Factory for creating DBI instances with the same configuration as in Dropwizard
 *
 * @author Artem Prigoda
 */
public class DropwizardDBIFactory implements DBIFactory {

    @Override
    public DBI createDBI(Properties properties) {
        DBI dbi = new DBI(properties.getProperty("db.url"), properties.getProperty("db.username"),
                properties.getProperty("db.password"));

        dbi.setSQLLog(new LogbackLog((Logger) LoggerFactory.getLogger(DBI.class), Level.INFO));
        dbi.setStatementRewriter(new NamePrependingStatementRewriter(new ColonPrefixNamedParamStatementRewriter()));

        dbi.registerArgumentFactory(new GuavaOptionalArgumentFactory(null));
        dbi.registerArgumentFactory(new OptionalArgumentFactory(null));
        dbi.registerArgumentFactory(new OptionalDoubleArgumentFactory());
        dbi.registerArgumentFactory(new OptionalIntArgumentFactory());
        dbi.registerArgumentFactory(new OptionalLongArgumentFactory());
        dbi.registerColumnMapper(new OptionalDoubleMapper());
        dbi.registerColumnMapper(new OptionalIntMapper());
        dbi.registerColumnMapper(new OptionalLongMapper());
        dbi.registerContainerFactory(new ImmutableListContainerFactory());
        dbi.registerContainerFactory(new ImmutableSetContainerFactory());
        dbi.registerContainerFactory(new GuavaOptionalContainerFactory());
        dbi.registerContainerFactory(new OptionalContainerFactory());

        Optional<TimeZone> timeZone = Optional.empty();
        dbi.registerArgumentFactory(new JodaDateTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new LocalDateArgumentFactory());
        dbi.registerArgumentFactory(new LocalDateTimeArgumentFactory());
        dbi.registerArgumentFactory(new InstantArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new OffsetDateTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new ZonedDateTimeArgumentFactory(timeZone));

        // Should be registered after GuavaOptionalArgumentFactory to be processed first
        dbi.registerArgumentFactory(new GuavaOptionalJodaTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new GuavaOptionalLocalDateArgumentFactory());
        dbi.registerArgumentFactory(new GuavaOptionalLocalDateTimeArgumentFactory());
        dbi.registerArgumentFactory(new GuavaOptionalInstantArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new GuavaOptionalOffsetTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new GuavaOptionalZonedTimeArgumentFactory(timeZone));

        // Should be registered after OptionalArgumentFactory to be processed first
        dbi.registerArgumentFactory(new OptionalJodaTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new OptionalLocalDateArgumentFactory());
        dbi.registerArgumentFactory(new OptionalLocalDateTimeArgumentFactory());
        dbi.registerArgumentFactory(new OptionalInstantArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new OptionalOffsetDateTimeArgumentFactory(timeZone));
        dbi.registerArgumentFactory(new OptionalZonedDateTimeArgumentFactory(timeZone));

        dbi.registerColumnMapper(new JodaDateTimeMapper(timeZone));
        dbi.registerColumnMapper(new InstantMapper(timeZone));
        dbi.registerColumnMapper(new LocalDateMapper());
        dbi.registerColumnMapper(new LocalDateTimeMapper());
        dbi.registerColumnMapper(new OffsetDateTimeMapper());
        dbi.registerColumnMapper(new ZonedDateTimeMapper());

        return dbi;
    }
}
