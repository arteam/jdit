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
            final Timed classTimed = clazz.getAnnotation(Timed.class);
            final Method method = extensionMethod.getMethod();
            final Timed methodTimed = method.getAnnotation(Timed.class);
            // If the method is metered, figure out the name
            if (methodTimed != null) {
                if (methodTimed.absolute()) {
                    return methodTimed.name();
                } else {
                    // We need to check if the class has a custom timer name
                    return classTimed == null || classTimed.name().isEmpty() ?
                            MetricRegistry.name(clazz, methodTimed.name()) :
                            MetricRegistry.name(classTimed.name(), methodTimed.name());
                }
            }
            // Maybe the class is metered?
            if (classTimed != null) {
                return classTimed.name().isEmpty() ? MetricRegistry.name(clazz, method.getName()) :
                        MetricRegistry.name(classTimed.name(), method.getName());
            }
        }
        return null;
    }
}
