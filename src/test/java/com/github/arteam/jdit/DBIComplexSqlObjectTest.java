package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.TeamSqlObject;
import com.github.arteam.jdit.domain.entity.Division;
import com.github.arteam.jdit.domain.entity.Player;
import com.github.arteam.jdit.domain.entity.Team;
import com.google.common.collect.ImmutableList;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

/**
 * Date: 1/22/15
 * Time: 8:57 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
@DataSet("teamDao/insert-divisions.sql")
public class DBIComplexSqlObjectTest {

    @TestedSqlObject
    TeamSqlObject teamSqlObject;

    @Test
    public void testBulkInsert() throws Exception {
        teamSqlObject.addTeam(new Team("St. Louis", Division.CENTRAL), ImmutableList.of(
                new Player("Vladimir", "Tarasenko", date("1991-04-01"), 184, 90),
                new Player("Jack", "Allen", date("1990-08-12"), 188, 85),
                new Player("David", "Backes", date("1985-03-06"), 188, 95)
        ));
        List<Player> players = teamSqlObject.getPlayers("St. Louis");
        for (Player player : players) {
            System.out.println(player);
        }
    }

    @Test
    public void testCheckNoData() {
        Assert.assertTrue(teamSqlObject.getPlayers("St. Louis").isEmpty());
    }

    private static Date date(String textDate) {
        return ISODateTimeFormat.date().parseDateTime(textDate).toDate();
    }
}