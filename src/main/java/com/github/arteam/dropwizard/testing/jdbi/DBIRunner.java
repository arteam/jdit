package com.github.arteam.dropwizard.testing.jdbi;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Date: 1/22/15
 * Time: 8:55 PM
 *
 * @author Artem Prigoda
 */
public class DBIRunner extends BlockJUnit4ClassRunner {

    private static SchemaMigration schemaMigration = new SchemaMigration();

    private DBIContext dbiContext;

    public DBIRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        Object test = super.createTest();
        injectTestObjects(test);

        return test;
    }

    private void injectTestObjects(Object test) throws IllegalAccessException {
        Field[] fields = test.getClass().getFields();
        if (fields == null) {
            return;
        }
        for (Field field : fields) {
            Annotation[] annotations = field.getAnnotations();
            if (annotations == null) {
                continue;
            }
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(DBIHandle.class)) {
                    if (!field.getGenericType().equals(Handle.class)) {
                        throw new IllegalArgumentException("Unable inject a DBI handle to a " +
                                "field with type " + field.getGenericType());
                    }
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalArgumentException("Unable inject a DBI Handle to a static field");
                    }
                    field.setAccessible(true);
                    field.set(test, dbiContext.getHandle());
                }
                if (annotation.annotationType().equals(DBIInstance.class)) {
                    if (!field.getGenericType().equals(DBI.class)) {
                        throw new IllegalArgumentException("Unable inject a DBI instance to " +
                                "a field with type " + field.getGenericType());
                    }
                    if (Modifier.isStatic(field.getModifiers())) {
                        throw new IllegalArgumentException("Unable inject a DBI instance to a static field");
                    }
                    field.setAccessible(true);
                    field.set(test, dbiContext.getDbi());
                }
            }
        }
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        final Class<?> testClass = getTestClass().getJavaClass();
        final Statement statement = super.classBlock(notifier);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // TODO Create database, DBI and handle, migrate populate schema
                dbiContext = new DBIContext();
                schemaMigration.migrate(dbiContext.getHandle());
                try {
                    statement.evaluate();
                } finally {
                    dbiContext.close();
                }
            }
        };
    }

    @Override
    protected Statement methodBlock(final FrameworkMethod method) {
        final Statement statement = super.methodBlock(method);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                // TODO Load data from method annotations
                statement.evaluate();
                // TODO Cleanup database
            }
        };
    }
}
