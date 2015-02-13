package com.github.arteam.dropwizard.testing.jdbi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 1/25/15
 * Time: 10:44 PM
 * <p/>
 * Annotation for creating and injecting a DBI SqlObject
 * to the current test.
 * <p/>
 * A field, which thin annotation marks, should be an
 * interface or an abstract class.
 *
 * @author Artem Prigoda
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TestedSqlObject {
}
