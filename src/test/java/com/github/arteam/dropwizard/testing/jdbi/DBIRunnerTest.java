package com.github.arteam.dropwizard.testing.jdbi;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Date: 1/22/15
 * Time: 8:57 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
public class DBIRunnerTest {

    @Test
    public void testHelloWorld(){
        System.out.println("Hello world!");
    }

    @Test
    public void testHelloDBI(){
        System.out.println("Hello DBI!");
    }
}