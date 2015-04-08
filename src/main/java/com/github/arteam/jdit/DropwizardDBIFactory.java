package com.github.arteam.jdit;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.base.Optional;
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
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.TimeZone;

/**
 * Date: 2/16/15
 * Time: 10:50 PM
 * Factory for creating DBI instances as in Dropwizard
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
        dbi.registerArgumentFactory(new OptionalArgumentFactory(null));
        dbi.registerContainerFactory(new ImmutableListContainerFactory());
        dbi.registerContainerFactory(new ImmutableSetContainerFactory());
        dbi.registerContainerFactory(new OptionalContainerFactory());
        dbi.registerArgumentFactory(new JodaDateTimeArgumentFactory());
        dbi.registerMapper(new JodaDateTimeMapper(Optional.<TimeZone>absent()));
        return dbi;
    }
}
