package com.github.arteam.dropwizard.testing.jdbi;

import com.github.arteam.dropwizard.testing.jdbi.annotations.DBIHandle;
import com.github.arteam.dropwizard.testing.jdbi.annotations.DBIInstance;
import com.github.arteam.dropwizard.testing.jdbi.annotations.TestedDao;
import com.github.arteam.dropwizard.testing.jdbi.annotations.TestedSqlObject;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Date: 1/25/15
 * Time: 11:56 PM
 * <p/>
 * Component for injecting test instances (DBI, handles, SQLObjects, DBI DAO)
 * to the fields with corresponding annotations in the test
 *
 * @author Artem Prigoda
 */
public class TestObjectsInjector {

    private DBI dbi;
    private Handle handle;

    public TestObjectsInjector(DBI dbi, Handle handle) {
        this.dbi = dbi;
        this.handle = handle;
    }

    /**
     * Inject test instances to the test
     * Search the test instance for field with annotations and inject test objects
     *
     * @param test current test
     * @throws IllegalAccessException reflection error
     */
    public void injectTestedInstances(Object test) throws IllegalAccessException {
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
                } else if (annotation.annotationType().equals(TestedDao.class)) {
                    handleDbiDao(test, field);
                } else if (annotation.annotationType().equals(TestedSqlObject.class)) {
                    handleDbiSqlObject(test, field);
                }
            }
        }
    }

    /**
     * Inject a DBI handle to a field with {@link DBIHandle} annotation
     *
     * @param test  current test
     * @param field current field
     * @throws IllegalAccessException reflection error
     */
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

    /**
     * Inject a DBI instance to a field with {@link DBIInstance} annotation
     *
     * @param test  current test
     * @param field current field
     * @throws IllegalAccessException reflection error
     */
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

    /**
     * Create and inject a new DBI SQL Object to a field
     * with {@link TestedSqlObject} annotation
     *
     * @param test  current test
     * @param field current field
     * @throws IllegalAccessException reflection error
     */
    private void handleDbiSqlObject(Object test, Field field) throws IllegalAccessException {
        if (!(field.getType().isInterface() ||
                Modifier.isAbstract(field.getType().getModifiers()))) {
            throw new IllegalArgumentException("Unable inject a DBI SQL object to a field with type '"
                    + field.getType() + "'");
        }
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Unable inject a DBI sql object to a static field");
        }
        field.setAccessible(true);
        field.set(test, handle.attach(field.getType()));
    }

    /**
     * Create a inject a DBI DAO instance to a field
     * with {@link TestedDao}  annotation
     * <p/>
     * The DAO should provide a default constructor or a constructor
     * that accepts a {@link DBI} as the single parameter
     *
     * @param test  current test
     * @param field current field
     * @throws IllegalAccessException reflection error
     */
    private void handleDbiDao(Object test, Field field) throws IllegalAccessException {
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Unable inject a DBI DAO to a static field");
        }

        field.setAccessible(true);
        field.set(test, createDBIDao(field));
    }

    private Object createDBIDao(Field field) throws IllegalAccessException {
        // Find appropriate constructors
        Constructor<?> defaultConstructor = null;
        for (Constructor<?> constructor : field.getType().getDeclaredConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0].equals(DBI.class)) {
                // If a constructor with a DBI is provided, just invoke it
                try {
                    constructor.setAccessible(true);
                    return constructor.newInstance(dbi);
                } catch (Exception e) {
                    throw new RuntimeException("Unable to create an instance of class '"
                            + field.getDeclaringClass() + "'", e);
                }
            } else if (parameterTypes.length == 0) {
                defaultConstructor = constructor;
            }
        }


        if (defaultConstructor == null) {
            // No eligible constructor is provided
            throw new IllegalStateException("Unable find a constructor for class '"
                    + field.getDeclaringClass() + "'");
        }
        // A default constructor is provided.
        // Invoke it, find a DBI field and set a DBI context to it
        Object dbiDao;
        defaultConstructor.setAccessible(true);
        try {
            dbiDao = defaultConstructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to create an instance of class '"
                    + field.getDeclaringClass() + "'", e);
        }

        for (Field classField : field.getType().getDeclaredFields()) {
            if (classField.getType().equals(DBI.class)) {
                classField.setAccessible(true);
                classField.set(dbiDao, dbi);
                return dbiDao;
            }
        }

        throw new IllegalStateException("Unable find a field with type DBI");
    }
}
