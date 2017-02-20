package com.github.arteam.jdbi3.strategies;

import com.codahale.metrics.MetricRegistry;
import org.jdbi.v3.core.extension.ExtensionMethod;
import org.jdbi.v3.core.statement.StatementContext;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DefaultNameStrategy implements StatementNameStrategy {

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

    NAIVE_NAME {
        @Override
        public String getStatementName(StatementContext statementContext) {
            return statementContext.getRawSql();
        }
    },

    CONSTANT_SQL_RAW {

        @Override
        public String getStatementName(StatementContext statementContext) {
            return "sql.raw";
        }
    },

    CONTEXT_CLASS {
        @Override
        public String getStatementName(StatementContext statementContext) {
            final Object classObj = statementContext.getAttribute(STATEMENT_CLASS);
            final Object nameObj = statementContext.getAttribute(STATEMENT_NAME);

            if (classObj == null || nameObj == null) {
                return null;
            }

            final String className = (String) classObj;
            final String statementName = (String) nameObj;

            final int dotPos = className.lastIndexOf('.');
            if (dotPos == -1) {
                return null;
            }

            return MetricRegistry.name(className.substring(0, dotPos),
                    className.substring(dotPos + 1),
                    statementName);
        }
    },
    CONTEXT_NAME {

        /**
         * File pattern to shorten the group name.
         */
        private final Pattern shortPattern = Pattern.compile("^(.*?)/(.*?)(-sql)?\\.st(g)?$");

        @Override
        public String getStatementName(StatementContext statementContext) {
            final Object groupObj = statementContext.getAttribute(STATEMENT_GROUP);
            final Object typeObj = statementContext.getAttribute(STATEMENT_TYPE);
            final Object nameObj = statementContext.getAttribute(STATEMENT_NAME);

            if (groupObj == null || nameObj == null) {
                return null;
            }

            final String group = (String) groupObj;
            final String statementName = (String) nameObj;

            if (typeObj == null) {
                final Matcher matcher = shortPattern.matcher(group);
                if (matcher.matches()) {
                    final String groupName = matcher.group(1);
                    final String typeName = matcher.group(2);
                    return MetricRegistry.name(groupName, typeName, statementName);
                }

                return MetricRegistry.name(group, statementName, "");
            } else {
                final String type = (String) typeObj;
                return MetricRegistry.name(group, type, statementName);
            }
        }
    };

    /**
     * Context attribute name for the metric class.
     */
    public static final String STATEMENT_CLASS = "_metric_class";

    /**
     * Context attribute name for the metric group.
     */
    public static final String STATEMENT_GROUP = "_metric_group";

    /**
     * Context attribute name for the metric type.
     */
    public static final String STATEMENT_TYPE = "_metric_type";

    /**
     * Context attribute name for the metric name.
     */
    public static final String STATEMENT_NAME = "_metric_name";

}
