package com.github.arteam.jdbi3.strategies;

import org.jdbi.v3.core.StatementContext;

/**
 * Interface for strategies to statement contexts to metric names.
 */
public interface StatementNameStrategy {
    String getStatementName(StatementContext statementContext);
}
