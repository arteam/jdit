package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import com.github.arteam.jdit.annotations.TestedDao;
import com.github.arteam.jdit.domain.PlayerDaoWithConstructor;
import org.jdbi.v3.core.Handle;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Date: 1/22/15
 * Time: 8:57 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
public class DBIDaoWithConstructorTest {

    @DBIHandle
    Handle handle;

    @TestedDao
    PlayerDaoWithConstructor playerDao;

    @Test
    public void testInsert() throws Exception {
        Long playerId = playerDao.createPlayer("Vladimir", "Tarasenko", new SimpleDateFormat("yyyy-MM-dd HH:mm:SS")
                .parse("1991-08-05 00:00:00"), 84, 99);

        String initials = handle.createQuery("select first_name || ' ' || last_name from players")
                .mapTo(String.class)
                .findOnly();
        Assert.assertEquals(initials, "Vladimir Tarasenko");
    }

    @Test
    public void testGetInitials() {
        List<String> lastNames = playerDao.getLastNames();
        Assert.assertTrue(lastNames.isEmpty());
    }
}