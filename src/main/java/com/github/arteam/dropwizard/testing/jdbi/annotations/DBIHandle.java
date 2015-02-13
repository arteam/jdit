package com.github.arteam.dropwizard.testing.jdbi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 1/24/15
 * Time: 12:24 AM
 * <p/>
 * Annotation for injecting a DBI handle to the current test.
 * A field, which this annotation marks, should have
 * type {@link org.skife.jdbi.v2.Handle}, otherwise an exception
 * will be raised during injecting.
 *
 * @author Artem Prigoda
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBIHandle {
}
