package com.github.arteam.dropwizard.testing.jdbi;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.tweak.HandleCallback;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Date: 1/22/15
 * Time: 8:55 PM
 *
 * @author Artem Prigoda
 */
public class DBIRunner extends BlockJUnit4ClassRunner {

    private static SchemaMigration schemaMigration = new SchemaMigration();

    private DBI dbi;
    private Handle handle;

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
        Field[] fields = test.getClass().getDeclaredFields();
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
                    handleDbiHandle(test, field);
                } else if (annotation.annotationType().equals(DBIInstance.class)) {
                    handleDbiInstance(test, field);
                } else if (annotation.annotationType().equals(TestedDBIDao.class)) {
                    handleDbiDao(test, field);
                }
            }
        }
    }

    private void handleDbiHandle(Object test, Field field) throws IllegalAccessException {
        if (!field.getType().equals(Handle.class)) {
            throw new IllegalArgumentException("Unable inject a DBI handle to a " +
                    "field with type " + field.getType());
        }
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Unable inject a DBI Handle to a static field");
        }
        field.setAccessible(true);
        field.set(test, handle);
    }

    private void handleDbiInstance(Object test, Field field) throws IllegalAccessException {
        if (!field.getType().equals(DBI.class)) {
            throw new IllegalArgumentException("Unable inject a DBI instance to " +
                    "a field with type " + field.getType());
        }
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Unable inject a DBI instance to a static field");
        }
        field.setAccessible(true);
        field.set(test, dbi);
    }

    private void handleDbiDao(Object test, Field field) throws IllegalAccessException {
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Unable inject a DBI DAO to a static field");
        }

        Object dbiDao;

        Constructor<?> defaultConstructor = null;
        Constructor<?> constructorWithParameters = null;
        Constructor<?>[] constructors = field.getType().getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0].equals(DBI.class)) {
                constructorWithParameters = constructor;
            } else if (parameterTypes.length == 0) {
                defaultConstructor = constructor;
            }
        }
        if (constructorWithParameters != null) {
            constructorWithParameters.setAccessible(true);
            try {
                dbiDao = constructorWithParameters.newInstance(dbi);
            } catch (Exception e) {
                throw new RuntimeException("Unable to create an instance of class '"
                        + field.getClass() + "'", e);
            }
        } else if (defaultConstructor != null) {
            defaultConstructor.setAccessible(true);
            try {
                dbiDao = defaultConstructor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Unable to create an instance of class '"
                        + field.getClass() + "'", e);
            }
            Field[] classFields = field.getType().getDeclaredFields();
            boolean dbiIsSet = false;
            if (classFields != null) {
                for (Field classField : classFields) {
                    if (classField.getType().equals(DBI.class)) {
                        classField.setAccessible(true);
                        classField.set(dbiDao, dbi);
                        dbiIsSet = true;
                        break;
                    }
                }
            }
            if (!dbiIsSet) {
                throw new IllegalStateException("Unable find a field with type DBI");
            }
        } else {
            throw new IllegalStateException("Unable find a constructor for class '"
                    + field.getDeclaringClass() + "'");
        }

        field.setAccessible(true);
        field.set(test, dbiDao);
    }

    @Override
    protected Statement classBlock(RunNotifier notifier) {
        final Statement statement = super.classBlock(notifier);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                dbi = DBIContext.createDBI();
                handle = dbi.open();
                try {
                    schemaMigration.migrate(handle);
                    statement.evaluate();
                } finally {
                    handle.close();
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
                try {
                    statement.evaluate();
                } finally {
                    handle.execute("TRUNCATE SCHEMA public AND COMMIT");
                }
            }
        };
    }
}
