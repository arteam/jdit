package com.github.arteam.jdbi3.strategies;


/**
 * Adds statistics for JDBI queries that set the {@link NameStrategies#STATEMENT_GROUP} and {@link
 * NameStrategies#STATEMENT_NAME} for group based display.
 */
public class ContextNameStrategy extends DelegatingStatementNameStrategy {

    public ContextNameStrategy() {
        super(NameStrategies.CHECK_EMPTY,
              NameStrategies.CONTEXT_NAME,
              NameStrategies.NAIVE_NAME);
    }
}
