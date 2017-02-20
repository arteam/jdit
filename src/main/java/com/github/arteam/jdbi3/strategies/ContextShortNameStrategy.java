package com.github.arteam.jdbi3.strategies;

import java.lang.reflect.Method;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Assembles all JDBI stats under a common prefix (passed in at constructor time). Stats are grouped
 * by class name and method; a shortening strategy is applied to make the JMX output nicer.
 */
public final class ContextShortNameStrategy extends DelegatingStatementNameStrategy {

    public ContextShortNameStrategy(String baseName) {
        registerStrategies(DefaultNameStrategy.CHECK_EMPTY,
                statementContext -> {
                    final Object classObj = statementContext.getAttribute(DefaultNameStrategy.STATEMENT_CLASS);
                    final Object nameObj = statementContext.getAttribute(DefaultNameStrategy.STATEMENT_NAME);

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
                DefaultNameStrategy.NAIVE_NAME);
    }
}
