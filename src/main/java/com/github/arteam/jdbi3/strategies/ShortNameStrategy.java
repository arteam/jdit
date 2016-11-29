package com.github.arteam.jdbi3.strategies;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Assembles all JDBI stats under a common prefix (passed in at constructor time). Stats are grouped
 * by class name and method; a shortening strategy is applied to make the JMX output nicer.
 */
public final class ShortNameStrategy extends DelegatingStatementNameStrategy {

    public ShortNameStrategy(String baseName) {
        registerStrategies(NameStrategies.CHECK_EMPTY,
                statementContext -> {
                    final Object classObj = statementContext.getAttribute(NameStrategies.STATEMENT_CLASS);
                    final Object nameObj = statementContext.getAttribute(NameStrategies.STATEMENT_NAME);

                    if (classObj == null || nameObj == null) {
                        return null;
                    }

                    final String className = (String) classObj;
                    final String statementName = (String) nameObj;

                    final int dotPos = className.lastIndexOf('.');
                    if (dotPos == -1) {
                        return null;
                    }

                    final String shortName = className.substring(dotPos + 1);
                    return name(baseName, shortName, statementName);
                },
                statementContext -> {
                    final Class<?> clazz = statementContext.getExtensionMethod().getType();
                    final Method method = statementContext.getExtensionMethod().getMethod();
                    if (clazz != null && method != null) {
                        final String className = clazz.getName();
                        final String statementName = method.getName();

                        final int dotPos = className.lastIndexOf('.');
                        if (dotPos == -1) {
                            return null;
                        }

                        final String shortName = className.substring(dotPos + 1);
                        return name(baseName, shortName, statementName);
                    }
                    return null;
                },
                NameStrategies.NAIVE_NAME);
    }
}
