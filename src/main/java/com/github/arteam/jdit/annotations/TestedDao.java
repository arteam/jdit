package com.github.arteam.jdit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 1/25/15
 * Time: 6:50 PM
 * <p/>
 * Annotation for creating and injecting a DBI DAO (class
 * with a {@link org.skife.jdbi.v2.DBI} field) to the
 * current test.
 *
 * @author Artem Prigoda
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TestedDao {
}
