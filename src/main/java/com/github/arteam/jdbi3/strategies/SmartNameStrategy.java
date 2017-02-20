package com.github.arteam.jdbi3.strategies;

/**
 * Adds statistics for JDBI queries that set the {@link DefaultNameStrategy#STATEMENT_CLASS} and {@link
 * DefaultNameStrategy#STATEMENT_NAME} for class based display or {@link DefaultNameStrategy#STATEMENT_GROUP}
 * and {@link DefaultNameStrategy#STATEMENT_NAME} for group based display.
 * <p/>
 * Also knows how to deal with SQL Object statements.
 */
public class SmartNameStrategy extends DelegatingStatementNameStrategy {

    public SmartNameStrategy() {
        super(DefaultNameStrategy.CHECK_EMPTY,
                DefaultNameStrategy.CONTEXT_CLASS,
                DefaultNameStrategy.CONTEXT_NAME,
                DefaultNameStrategy.SQL_OBJECT,
                DefaultNameStrategy.CONSTANT_SQL_RAW);
    }
}
