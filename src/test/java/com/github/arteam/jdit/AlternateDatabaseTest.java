package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import org.jdbi.v3.core.Handle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DBIExtension.class)
public abstract class AlternateDatabaseTest {

    private static final DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE;

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @DBIHandle
    Handle handle;

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testCreatePlayer() {
        Long playerId = playerDao.createPlayer("Colton", "Parayko", date("1993-05-12"),
                196, 102);
        assertThat(playerId).isEqualTo(2L);
        assertThat(handle.select("select * from players where id=?", 2)
                .mapToMap()
                .one()).containsEntry("id", 2)
                .containsEntry("first_name", "Colton")
                .containsEntry("last_name", "Parayko")
                .containsEntry("weight", 102)
                .containsEntry("height", 196)
                .containsEntry("birth_date", date(("1993-05-12")));
    }

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testCreateAnotherPlayer() {
        Long playerId = playerDao.createPlayer("Robby", "Fabbri", date(("1996-01-22")),
                178, 75);
        assertThat(playerId).isEqualTo(2L);
        assertThat(handle.select("select * from players where id=?", 2)
                .mapToMap()
                .one()).containsEntry("id", 2)
                .containsEntry("first_name", "Robby")
                .containsEntry("last_name", "Fabbri")
                .containsEntry("weight", 75)
                .containsEntry("height", 178)
                .containsEntry("birth_date", date(("1996-01-22")));
    }


    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testGetLastNames() {
        assertThat(playerDao.getLastNames()).containsOnly("Tarasenko");
    }

    private static Date date(String textDate) {
        return Date.from(LocalDate.parse(textDate, fmt).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
