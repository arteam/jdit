package com.github.arteam.dropwizard.testing.jdbi;

import com.github.arteam.dropwizard.testing.jdbi.annotations.DBIHandle;
import com.github.arteam.dropwizard.testing.jdbi.annotations.TestedSqlObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.StringMapper;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Date: 1/22/15
 * Time: 8:57 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
public class DBISqlObjectTest {

    @DBIHandle
    Handle handle;

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    public void testInsert() throws Exception {
        System.out.println("Hello DBI!");
        Long playerId = playerDao.createPlayer("Vladimir", "Tarasenko", new SimpleDateFormat("yyyy-MM-dd HH:mm:SS")
                .parse("1991-08-05 00:00:00"), 84, 99);
        System.out.println(playerId);

        String initials = handle.createQuery("select first_name || ' ' || last_name from players")
                .map(StringMapper.FIRST)
                .first();
        System.out.println(initials);
        Assert.assertEquals(initials, "Vladimir Tarasenko");
    }


    @Test
    public void testGetInitials() {
        List<String> lastNames = playerDao.getLastNames();
        System.out.println(lastNames);
        Assert.assertTrue(lastNames.isEmpty());
    }
}