package com.github.arteam.jdbi3.strategies;

/**
 * Adds statistics for JDBI queries that set the {@link DefaultNameStrategy#STATEMENT_GROUP} and {@link
 * DefaultNameStrategy#STATEMENT_NAME} for group based display.
 */
public class ContextNameStrategy extends DelegatingStatementNameStrategy {

    public ContextNameStrategy() {
        super(DefaultNameStrategy.CHECK_EMPTY,
              DefaultNameStrategy.CONTEXT_NAME,
              DefaultNameStrategy.NAIVE_NAME);
    }
}
