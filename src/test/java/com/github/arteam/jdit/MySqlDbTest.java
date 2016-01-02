package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.JditProperties;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import com.google.common.collect.ImmutableList;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skife.jdbi.v2.Handle;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Date: 1/1/16
 * Time: 7:51 PM
 *
 * @author Artem Prigoda
 */
@JditProperties("jdit-mysql.properties")
@RunWith(DBIRunner.class)
public class MySqlDbTest {

    private static final DateTimeFormatter fmt = ISODateTimeFormat.date().withZoneUTC();

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @DBIHandle
    Handle handle;

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testCreatePlayer() {
        Long playerId = playerDao.createPlayer("Colton", "Parayko", fmt.parseDateTime("1993-05-12").toDate(),
                196, 102);
        assertEquals(playerId.longValue(), 2L);

        List<Map<String, Object>> rows = handle.select("select * from players where id=?", 2);
        assertEquals(rows.size(), 1);
        Map<String, Object> row = rows.get(0);
        assertEquals(row.get("id"), 2);
        assertEquals(row.get("first_name"), "Colton");
        assertEquals(row.get("last_name"), "Parayko");
        assertEquals(row.get("weight"), 102);
        assertEquals(row.get("height"), 196);
        assertEquals(row.get("birth_date").toString(), "1993-05-12");
    }

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testCreateAnotherPlayer() {
        Long playerId = playerDao.createPlayer("Robby", "Fabbri", fmt.parseDateTime("1996-01-22").toDate(),
                178, 75);
        assertEquals(playerId.longValue(), 2L);

        List<Map<String, Object>> rows = handle.select("select * from players where id=?", 2);
        assertEquals(rows.size(), 1);
        Map<String, Object> row = rows.get(0);
        assertEquals(row.get("id"), 2);
        assertEquals(row.get("first_name"), "Robby");
        assertEquals(row.get("last_name"), "Fabbri");
        assertEquals(row.get("weight"), 75);
        assertEquals(row.get("height"), 178);
        assertEquals(row.get("birth_date").toString(), "1996-01-22");
    }

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testGetLastNames() {
        List<String> lastNames = playerDao.getLastNames();
        assertEquals(lastNames, ImmutableList.of("Tarasenko"));
    }

}
