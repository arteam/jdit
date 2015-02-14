package com.github.arteam.jdit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 1/26/15
 * Time: 12:02 AM
 * <p/>
 * Annotation for marking that a data set should be
 * loaded from a location before the method execution
 *
 * @author Artem Prigoda
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DataSet {

    /**
     * Location of the data set file. It should not start with "/".
     * For example, "dao/players.sql" is the correct way
     * of defining the file location
     *
     * @return the location of the data set file
     */
    String value();
}
