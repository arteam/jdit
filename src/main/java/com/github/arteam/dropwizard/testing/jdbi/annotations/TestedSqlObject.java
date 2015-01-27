package com.github.arteam.dropwizard.testing.jdbi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 1/25/15
 * Time: 10:44 PM
 * Inject DBI SqlObject to a test
 *
 * @author Artem Prigoda
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TestedSqlObject {
}
