package com.github.arteam.jdbi3.strategies;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import org.jdbi.v3.core.extension.ExtensionMethod;
import org.jdbi.v3.core.statement.StatementContext;

import java.lang.reflect.Method;

/**
 * Default strategies which build a basis of more complex strategies
 */
public enum DefaultNameStrategy implements StatementNameStrategy {

    /**
     * If no SQL in the context, returns `sql.empty`, otherwise falls through
     */
    CHECK_EMPTY {
        @Override
        public String getStatementName(StatementContext statementContext) {
            final String rawSql = statementContext.getRawSql();
            if (rawSql == null || rawSql.length() == 0) {
                return "sql.empty";
            }
            return null;
        }
    },

    /**
     * If there is an SQL object attached to the context, returns the name package,
     * the class and the method on which SQL is declared. If not SQL object is attached,
     * falls through
     */
    SQL_OBJECT {
        @Override
        public String getStatementName(StatementContext statementContext) {
            ExtensionMethod extensionMethod = statementContext.getExtensionMethod();
            if (extensionMethod != null) {
                final Class<?> clazz = extensionMethod.getType();
                final Method method = extensionMethod.getMethod();
                final String group = clazz.getPackage().getName();
                final String name = clazz.getSimpleName();
                return MetricRegistry.name(group, name, method.getName());
            }
            return null;
        }
    },

    /**
     * Takes into account the {@link Timed} annotation on extension methods
     */
    TIMED_SQL_OBJECT {
        @Override
        public String getStatementName(StatementContext statementContext) {
            ExtensionMethod extensionMethod = statementContext.getExtensionMethod();
            if (extensionMethod != null) {
                final Class<?> clazz = extensionMethod.getType();
                final Method method = extensionMethod.getMethod();
                final Timed timed = method.getAnnotation(Timed.class);
                if (timed != null) {
                    return timed.absolute() ? timed.name() : MetricRegistry.name(clazz, timed.name());
                } else {
                    return MetricRegistry.name(clazz, method.getName());
                }
            }
            return null;
        }
    },

    /**
     * Returns a raw SQL in the context (even if it's not exist)
     */
    NAIVE_NAME {
        @Override
        public String getStatementName(StatementContext statementContext) {
            return statementContext.getRawSql();
        }
    },

    /**
     * Returns the `sql.raw` constant
     */
    CONSTANT_SQL_RAW {
        @Override
        public String getStatementName(StatementContext statementContext) {
            return "sql.raw";
        }
    }

}
