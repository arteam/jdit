package com.github.arteam.dropwizard.testing.jdbi;

import com.github.arteam.dropwizard.testing.jdbi.annotations.DBIHandle;
import com.github.arteam.dropwizard.testing.jdbi.annotations.TestedSqlObject;
import com.github.arteam.dropwizard.testing.jdbi.domain.TeamSqlObject;
import com.github.arteam.dropwizard.testing.jdbi.domain.entity.Division;
import com.github.arteam.dropwizard.testing.jdbi.domain.entity.Player;
import com.github.arteam.dropwizard.testing.jdbi.domain.entity.Team;
import com.google.common.collect.ImmutableList;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skife.jdbi.v2.Handle;

import java.util.Date;
import java.util.List;

/**
 * Date: 1/22/15
 * Time: 8:57 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
public class DBIComplexSqlObjectTest {

    @DBIHandle
    Handle handle;

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