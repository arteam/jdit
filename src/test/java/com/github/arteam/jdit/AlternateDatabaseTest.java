package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import org.jdbi.v3.core.Handle;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DBIExtension.class)
public abstract class AlternateDatabaseTest {

    private static final DateTimeFormatter fmt = ISODateTimeFormat.date();

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @DBIHandle
    Handle handle;

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testCreatePlayer() {
        Long playerId = playerDao.createPlayer("Colton", "Parayko", fmt.parseDateTime("1993-05-12").toDate(),
                196, 102);
        assertThat(playerId).isEqualTo(2L);
        assertThat(handle.select("select * from players where id=?", 2)
                .mapToMap()
                .one()).containsEntry("id", 2)
                .containsEntry("first_name", "Colton")
                .containsEntry("last_name", "Parayko")
                .containsEntry("weight", 102)
                .containsEntry("height", 196)
                .containsEntry("birth_date", fmt.parseDateTime("1993-05-12").toDate());
    }

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testCreateAnotherPlayer() {
        Long playerId = playerDao.createPlayer("Robby", "Fabbri", fmt.parseDateTime("1996-01-22").toDate(),
                178, 75);
        assertThat(playerId).isEqualTo(2L);
        assertThat(handle.select("select * from players where id=?", 2)
                .mapToMap()
                .one()).containsEntry("id", 2)
                .containsEntry("first_name", "Robby")
                .containsEntry("last_name", "Fabbri")
                .containsEntry("weight", 75)
                .containsEntry("height", 178)
                .containsEntry("birth_date", fmt.parseDateTime("1996-01-22").toDate());
    }

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testGetLastNames() {
        assertThat(playerDao.getLastNames()).containsOnly("Tarasenko");
    }
}
