package com.github.arteam.jdbi3.strategies;

import org.jdbi.v3.core.ExtensionMethod;
import org.jdbi.v3.core.StatementContext;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codahale.metrics.MetricRegistry.name;

public class NameStrategies {

    /**
     * File pattern to shorten the group name.
     */
    private static final Pattern SHORT_PATTERN = Pattern.compile("^(.*?)/(.*?)(-sql)?\\.st(g)?$");

    private static final String SQL_EMPTY = "sql.empty";
    private static final String SQL_RAW = "sql.raw";

    /**
     * Unknown SQL.
     */
    static final String UNKNOWN_SQL = "sql.unknown";

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

    public static final StatementNameStrategy CHECK_EMPTY = statementContext -> {
        final String rawSql = statementContext.getRawSql();
        if (rawSql == null || rawSql.length() == 0) {
            return SQL_EMPTY;
        }
        return null;
    };

    public static final StatementNameStrategy SQL_OBJECT = statementContext -> {
        ExtensionMethod extensionMethod = statementContext.getExtensionMethod();
        if (extensionMethod != null) {
            final Class<?> clazz = extensionMethod.getType();
            final Method method = extensionMethod.getMethod();
            final String group = clazz.getPackage().getName();
            final String name = clazz.getSimpleName();
            return name(group, name, method.getName());
        }
        return null;
    };

    public static final StatementNameStrategy NAIVE_NAME = StatementContext::getRawSql;

    public static final StatementNameStrategy CONSTANT_SQL_RAW = statementContext -> SQL_RAW;

    public static final StatementNameStrategy CONTEXT_CLASS = statementContext -> {
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

        return name(className.substring(0, dotPos),
                className.substring(dotPos + 1),
                statementName);
    };

    public static final StatementNameStrategy CONTEXT_NAME = statementContext -> {
        final Object groupObj = statementContext.getAttribute(STATEMENT_GROUP);
        final Object typeObj = statementContext.getAttribute(STATEMENT_TYPE);
        final Object nameObj = statementContext.getAttribute(STATEMENT_NAME);

        if (groupObj == null || nameObj == null) {
            return null;
        }

        final String group = (String) groupObj;
        final String statementName = (String) nameObj;

        if (typeObj == null) {
            final Matcher matcher = SHORT_PATTERN.matcher(group);
            if (matcher.matches()) {
                final String groupName = matcher.group(1);
                final String typeName = matcher.group(2);
                return name(groupName, typeName, statementName);
            }

            return name(group, statementName, "");
        } else {
            final String type = (String) typeObj;
            return name(group, type, statementName);
        }
    };
}
