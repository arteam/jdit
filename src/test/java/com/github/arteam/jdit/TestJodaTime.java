package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import com.github.arteam.jdit.domain.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
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

    private static final DateTimeFormatter FMT = ISODateTimeFormat.date().withZoneUTC();

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    public void testDateTimeParameter() {
        DateTime dateTime = FMT.parseDateTime("1991-04-01");
        List<Player> players = playerDao.getPlayersBornAfter(dateTime);
        Assert.assertEquals(players.size(), 3);
        for (Player player : players) {
            Assert.assertTrue(player.birthDate.after(dateTime.toDate()));
        }
    }

    @Test
    public void testJodaTimeResponse() {
        DateTime birthDate = playerDao.getPlayerBirthDate("Vladimir", "Tarasenko");
        Assert.assertEquals(1991, birthDate.getYear());
        Assert.assertEquals(8, birthDate.getMonthOfYear());
        Assert.assertEquals(5, birthDate.getDayOfMonth());
    }
}
