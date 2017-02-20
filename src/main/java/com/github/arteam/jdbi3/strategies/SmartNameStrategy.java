package com.github.arteam.jdbi3.strategies;

/**
 * Collects metrics for SQL Objects and fallbacks to the `sql.raw` constant is no
 * SQL object is present.
 */
public class SmartNameStrategy extends DelegatingStatementNameStrategy {

    public SmartNameStrategy() {
        super(DefaultNameStrategy.CHECK_EMPTY,
                DefaultNameStrategy.SQL_OBJECT,
                DefaultNameStrategy.CONSTANT_SQL_RAW);
    }
}
