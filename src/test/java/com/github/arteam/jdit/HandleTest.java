package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skife.jdbi.v2.Handle;

import java.util.List;

/**
 * Date: 1/25/15
 * Time: 9:50 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
public class HandleTest {

    @DBIHandle
    Handle handle;

    @Test
    public void testInsert() {
        int amount = handle.insert("insert into players(first_name, last_name, birth_date, weight, height)" +
                " values ('Vladimir','Tarasenko', '1991-08-05', 84, 99)");
        Assert.assertEquals(amount, 1);

        String initials = handle.createQuery("select first_name || ' ' || last_name from players")
                .mapTo(String.class)
                .first();
        System.out.println(initials);
        Assert.assertEquals(initials, "Vladimir Tarasenko");
    }

    @Test
    public void testGetInitials() {
        List<String> lastNames = handle.createQuery("select last_name from players")
                .mapTo(String.class)
                .list();
        System.out.println(lastNames);
        Assert.assertTrue(lastNames.isEmpty());
    }
}
