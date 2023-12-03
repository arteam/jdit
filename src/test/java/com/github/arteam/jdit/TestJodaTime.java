package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import com.github.arteam.jdit.domain.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DBIExtension.class)
@DataSet("playerDao/players.sql")
public class TestJodaTime {

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    public void testDateTimeParameter() {
        LocalDate dateTime = LocalDate.parse("1991-04-01", DateTimeFormatter.ISO_DATE);
        List<Player> players = playerDao.getPlayersBornAfter(dateTime);
        assertThat(players)
                .hasSize(3)
                .extracting(p -> p.birthDate())
                .allMatch(d -> d.toInstant().isAfter(dateTime.atStartOfDay().toInstant(ZoneOffset.UTC)));
    }

    @Test
    public void testJodaTimeResponse() {
        LocalDate birthDate = playerDao.getPlayerBirthDate("Vladimir", "Tarasenko");
        assertThat(birthDate.getYear()).isEqualTo(1991);
        assertThat(birthDate.getMonthValue()).isEqualTo(8);
        assertThat(birthDate.getDayOfMonth()).isEqualTo(5);
    }
}
