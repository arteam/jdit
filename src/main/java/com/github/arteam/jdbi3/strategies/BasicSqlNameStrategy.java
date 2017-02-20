package com.github.arteam.jdbi3.strategies;

public class BasicSqlNameStrategy extends DelegatingStatementNameStrategy {

    public BasicSqlNameStrategy() {
        super(DefaultNameStrategy.CHECK_EMPTY,
                DefaultNameStrategy.SQL_OBJECT);
    }
}
