package com.github.arteam.jdbi3.strategies;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import org.jdbi.v3.core.extension.ExtensionMethod;
import org.jdbi.v3.core.statement.StatementContext;

import java.lang.reflect.Method;

/**
 * Takes into account the {@link Timed} annotation on extension methods
 */
public class TimedAnnotationNameStrategy implements StatementNameStrategy {

    @Override
    public String getStatementName(StatementContext statementContext) {
        ExtensionMethod extensionMethod = statementContext.getExtensionMethod();
        if (extensionMethod != null) {
            final Class<?> clazz = extensionMethod.getType();
            final Method method = extensionMethod.getMethod();
            final Timed timed = method.getAnnotation(Timed.class);
            if (timed != null) {
                return timed.absolute() ? timed.name() : MetricRegistry.name(clazz, timed.name());
            }
        }
        return null;
    }
}
