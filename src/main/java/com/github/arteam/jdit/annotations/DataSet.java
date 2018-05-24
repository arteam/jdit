package com.github.arteam.jdit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for marking that a data set should be loaded from a location before the method execution
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DataSet {

    /**
     * Location of the data set files. It should NOT start with "/".
     * For example, "dao/players.sql" is the correct way of defining a file location.
     *
     * @return the location of the data set files
     */
    String[] value();
}
