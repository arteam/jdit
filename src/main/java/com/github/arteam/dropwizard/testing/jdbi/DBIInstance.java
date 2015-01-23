package com.github.arteam.dropwizard.testing.jdbi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 1/24/15
 * Time: 12:47 AM
 * Inject DBI instance to a test
 *
 * @author Artem Prigoda
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBIInstance {
}
