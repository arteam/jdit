package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.TeamSqlObject;
import com.github.arteam.jdit.domain.entity.Division;
import com.github.arteam.jdit.domain.entity.Player;
import com.github.arteam.jdit.domain.entity.Team;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DBIExtension.class)
@DataSet("teamDao/insert-divisions.sql")
public class DBIComplexSqlObjectTest {

    @TestedSqlObject
    TeamSqlObject teamSqlObject;

    @Test
    public void testBulkInsert() throws Exception {
        teamSqlObject.addTeam(new Team("St. Louis", Division.CENTRAL), List.of(
                new Player("Vladimir", "Tarasenko", date("1991-04-01"), 184, 90),
                new Player("Jack", "Allen", date("1990-08-12"), 188, 85),
                new Player("David", "Backes", date("1985-03-06"), 188, 95)
        ));
        List<Player> players = teamSqlObject.getPlayers("St. Louis");
        assertThat(players).hasSize(3);
        assertThat(players).extracting(p -> p.firstName).containsOnly("Vladimir", "Jack", "David");
        assertThat(players).extracting(p -> p.lastName).containsOnly("Tarasenko", "Allen", "Backes");
    }

    @Test
    public void testCheckNoData() {
        assertThat(teamSqlObject.getPlayers("St. Louis")).isEmpty();
    }

    private static Date date(String textDate) {
        return ISODateTimeFormat.date().parseDateTime(textDate).toDate();
    }
}