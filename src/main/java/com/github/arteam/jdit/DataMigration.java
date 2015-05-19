package com.github.arteam.jdit;

import org.skife.jdbi.v2.Binding;
import org.skife.jdbi.v2.ColonPrefixNamedParamStatementRewriter;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.RewrittenStatement;
import org.skife.jdbi.v2.tweak.StatementLocator;
import org.skife.jdbi.v2.tweak.StatementRewriter;

/**
 * Component that's responsible for migrating data in the DB
 */
class DataMigration {

    private final Handle handle;

    public DataMigration(Handle handle) {
        this.handle = handle;
    }

    /**
     * Execute a script a from a classpath location
     *
     * @param scriptLocation script location (without leading slash).
     *                       If one exists, it's trimmed
     */
    public void executeScript(String scriptLocation) {
        String correctLocation = !scriptLocation.startsWith("/") ?
                scriptLocation : scriptLocation.substring(1);
        handle.createScript(correctLocation).executeAsSeparateStatements();
    }

    /**
     * Sweep data from DB, but don't drop the schema.
     * Also restart sequences, so tests can rely on their predictability
     */
    public void sweepData() {
        handle.execute("TRUNCATE SCHEMA public RESTART IDENTITY AND COMMIT");
    }

    public void executeRewrittenScript(String location) {
        handle.setStatementRewriter(new StatementRewriter() {

            private StatementRewriter statementRewriter = new ColonPrefixNamedParamStatementRewriter();

            @Override
            public RewrittenStatement rewrite(String sql, Binding params, StatementContext ctx) {
                String replacedSql = sql.replaceAll("insert into (\\S+)", "insert into expected_$1");
                return statementRewriter.rewrite(replacedSql, params, ctx);
            }
        });
        handle.createScript(location).executeAsSeparateStatements();
    }
}
