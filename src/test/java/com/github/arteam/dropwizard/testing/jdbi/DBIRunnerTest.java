package com.github.arteam.dropwizard.testing.jdbi;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skife.jdbi.v2.Handle;

/**
 * Date: 1/22/15
 * Time: 8:57 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
public class DBIRunnerTest {

    @DBIHandle
    Handle handle;

    private final String helloDBI = "Hello DBI!";

    @Test
    public void testHelloWorld() {
        System.out.println("Hello world!");
    }

    @Test
    public void testHelloDBI() {
        System.out.println(helloDBI);
        int amount = handle.insert("insert into players(first_name, last_name, birth_date, weight, height)" +
                " values ('Vladimir','Tarasenko', '1991-08-05 00:00:00', 84, 99)");
        Assert.assertEquals(amount, 1);
    }
}