package com.github.arteam.jdit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for injecting a DBI instance to the current test. A field, which this
 * annotation marks, should have type {@link org.jdbi.v3.core.Jdbi}, otherwise an exception
 * will be raised
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBIInstance {
}
