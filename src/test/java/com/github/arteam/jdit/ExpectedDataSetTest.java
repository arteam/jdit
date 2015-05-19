package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.ExpectedDataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;

/**
 * Date: 1/22/15
 * Time: 8:57 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
@Ignore
public class ExpectedDataSetTest {

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    @ExpectedDataSet("playerDao/getInitials.sql")
    public void testInsert() throws Exception {
        Long playerId = playerDao.createPlayer("Vladimir", "Tarasenko", new SimpleDateFormat("yyyy-MM-dd HH:mm:SS")
                .parse("1991-08-05 00:00:00"), 84, 99);
        System.out.println(playerId);
    }

}