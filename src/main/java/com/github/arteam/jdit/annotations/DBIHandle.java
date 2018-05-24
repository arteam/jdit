package com.github.arteam.jdit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for injecting a DBI handle to the current test. A field, which this
 * annotation marks, should have type {@link org.jdbi.v3.core.Handle}, otherwise
 * an exception will be raised during injecting.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBIHandle {
}
