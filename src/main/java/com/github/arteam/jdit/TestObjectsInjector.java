package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import com.github.arteam.jdit.annotations.DBIInstance;
import com.github.arteam.jdit.annotations.TestedDao;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A component that injects test instances (DBI, Handles, SQLObjects, JDBI DAOs)
 * to the fields with corresponding annotations in the test.
 */
class TestObjectsInjector {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private final Jdbi dbi;
    private final Handle handle;

    TestObjectsInjector(Jdbi dbi, Handle handle) {
        this.dbi = dbi;
        this.handle = handle;
    }

    /**
     * Inject test instances to the test.
     * Search the test instance for field with annotations and inject test objects.
     *
     * @param test current test
     * @throws IllegalAccessException reflection error
     */
    void injectTestedInstances(Object test) throws IllegalAccessException {
        // TODO Cache reflection information
        List<Field> fields = new ArrayList<>();
        Class<?> currentTestClass = test.getClass();
        while (true) {
            Collections.addAll(fields, currentTestClass.getDeclaredFields());
            Class<?> superClass = test.getClass().getSuperclass();
            if (superClass.equals(currentTestClass)) {
                break;
            }
            currentTestClass = superClass;
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
     * Inject a DBI handle to a field with a {@link DBIHandle} annotation.
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
        MethodHandles.privateLookupIn(field.getDeclaringClass(), LOOKUP)
                .unreflectVarHandle(field)
                .set(test, handle);
    }

    /**
     * Inject a DBI instance to a field with a {@link DBIInstance} annotation.
     *
     * @param test  current test
     * @param field current field
     * @throws IllegalAccessException reflection error
     */
    private void handleDbiInstance(Object test, Field field) throws IllegalAccessException {
        if (!field.getType().equals(Jdbi.class)) {
            throw new IllegalArgumentException("Unable inject a DBI instance to " +
                    "a field with type " + field.getType());
        }
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Unable inject a DBI instance to a static field");
        }
        MethodHandles.privateLookupIn(field.getDeclaringClass(), LOOKUP)
                .unreflectVarHandle(field)
                .set(test, dbi);
    }

    /**
     * Create and inject a new DBI SQL Object to a field witha  {@link TestedSqlObject} annotation.
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
        MethodHandles.privateLookupIn(field.getDeclaringClass(), LOOKUP)
                .unreflectVarHandle(field)
                .set(test, handle.attach(field.getType()));
    }

    /**
     * Create a inject a DBI DAO instance to a field with the {@link TestedDao} annotation.
     * The DAO should provide a default constructor or a constructor that accepts a {@link Jdbi}
     * as the single parameter
     *
     * @param test  current test
     * @param field current field
     * @throws IllegalAccessException reflection error
     */
    private void handleDbiDao(Object test, Field field) throws IllegalAccessException {
        if (Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Unable inject a DBI DAO to a static field");
        }

        MethodHandles.privateLookupIn(test.getClass(), LOOKUP)
                .unreflectVarHandle(field)
                .set(test, createDBIDao(field));
    }

    private Object createDBIDao(Field field) throws IllegalAccessException {
        // Find appropriate constructors
        Constructor<?> defaultConstructor = null;
        for (Constructor<?> constructor : field.getType().getDeclaredConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length == 1 && parameterTypes[0].equals(Jdbi.class)) {
                // If a constructor with a DBI is provided, just invoke it
                try {
                    return MethodHandles.privateLookupIn(constructor.getDeclaringClass(), LOOKUP)
                            .unreflectConstructor(constructor)
                            .invoke(dbi);
                } catch (Throwable t) {
                    throw new RuntimeException("Unable to create an instance of class '"
                            + field.getDeclaringClass() + "'", t);
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
        // Invoke it, find a DBI field and set a DBI context to it.
        Object dbiDao;
        try {
            dbiDao = MethodHandles.privateLookupIn(defaultConstructor.getDeclaringClass(), LOOKUP)
                    .unreflectConstructor(defaultConstructor)
                    .invoke();
        } catch (Throwable t) {
            throw new RuntimeException("Unable to create an instance of class '"
                    + field.getDeclaringClass() + "'", t);
        }

        for (Field classField : field.getType().getDeclaredFields()) {
            if (classField.getType().equals(Jdbi.class)) {
                MethodHandles.privateLookupIn(classField.getDeclaringClass(), LOOKUP)
                        .unreflectVarHandle(classField)
                        .set(dbiDao, dbi);
                return dbiDao;
            }
        }

        throw new IllegalStateException("Unable find a field with type DBI");
    }
}
