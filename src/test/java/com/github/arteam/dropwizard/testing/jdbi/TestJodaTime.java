package com.github.arteam.dropwizard.testing.jdbi;

import com.github.arteam.dropwizard.testing.jdbi.annotations.DataSet;
import com.github.arteam.dropwizard.testing.jdbi.annotations.TestedSqlObject;
import com.github.arteam.dropwizard.testing.jdbi.domain.PlayerSqlObject;
import com.github.arteam.dropwizard.testing.jdbi.domain.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Date: 2/11/15
 * Time: 11:50 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
@DataSet("playerDao/players.sql")
public class TestJodaTime {

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    public void testDateTimeParameter() {
        DateTime dateTime = ISODateTimeFormat.date().parseDateTime("1991-04-01");
        List<Player> players = playerDao.getPlayersBornAfter(dateTime);
        System.out.println(players);
        Assert.assertEquals(players.size(), 3);
        for (Player player : players) {
            Assert.assertTrue(player.birthDate.after(dateTime.toDate()));
        }
    }
}
