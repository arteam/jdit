package com.github.arteam.jdit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for creating and injecting a DBI SqlObject to the current test.
 * A field, which thin annotation marks, should be an interface or an abstract class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TestedSqlObject {
}
