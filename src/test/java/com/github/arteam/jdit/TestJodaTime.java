package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import com.github.arteam.jdit.domain.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DBIRunner.class)
@DataSet("playerDao/players.sql")
public class TestJodaTime {

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    public void testDateTimeParameter() {
        DateTime dateTime = ISODateTimeFormat.date().withZoneUTC().parseDateTime("1991-04-01");
        List<Player> players = playerDao.getPlayersBornAfter(dateTime);
        assertThat(players)
                .hasSize(3)
                .extracting(p -> p.birthDate)
                .allSatisfy(d -> d.after(dateTime.toDate()));
    }

    @Test
    public void testJodaTimeResponse() {
        DateTime birthDate = playerDao.getPlayerBirthDate("Vladimir", "Tarasenko");
        assertThat(birthDate.getYear()).isEqualTo(1991);
        assertThat(birthDate.getMonthOfYear()).isEqualTo(8);
        assertThat(birthDate.getDayOfMonth()).isEqualTo(5);
    }
}
