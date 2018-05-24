package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.JditProperties;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import com.google.common.collect.ImmutableList;
import org.jdbi.v3.core.Handle;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

@JditProperties("jdit-mysql.properties")
@RunWith(DBIRunner.class)
@Ignore
public class MySqlDbTest {

    @BeforeClass
    public static void beforeInit() {
        assumeTrue(Boolean.parseBoolean(System.getenv("TRAVIS")));
    }

    private static final DateTimeFormatter fmt = ISODateTimeFormat.date().withZoneUTC();

    @TestedSqlObject
    private PlayerSqlObject playerDao;

    @DBIHandle
    private Handle handle;

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testCreatePlayer() {
        Long playerId = playerDao.createPlayer("Joel", "Edmunson",
                date("1993-06-28"), 193, 94);
        assertEquals(playerId.longValue(), 2L);

        Map<String, Object> row = handle.select("select * from players where id=?", 2)
                .mapToMap()
                .findOnly();
        assertEquals(row.get("id"), 2);
        assertEquals(row.get("first_name"), "Joel");
        assertEquals(row.get("last_name"), "Edmunson");
        assertEquals(row.get("weight"), 94);
        assertEquals(row.get("height"), 193);
        assertEquals(row.get("birth_date").toString(), "1993-06-28");
    }

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testCreateAnotherPlayer() {
        Long playerId = playerDao.createPlayer("Nail", "Yakupov",
                date("1993-10-06"), 178, 75);
        assertEquals(playerId.longValue(), 2L);

        Map<String, Object> row = handle.select("select * from players where id=?", 2)
                .mapToMap()
                .findOnly();
        assertEquals(row.get("id"), 2);
        assertEquals(row.get("first_name"), "Nail");
        assertEquals(row.get("last_name"), "Yakupov");
        assertEquals(row.get("weight"), 75);
        assertEquals(row.get("height"), 178);
        assertEquals(row.get("birth_date").toString(), "1993-10-06");
    }

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testGetLastNames() {
        List<String> lastNames = playerDao.getLastNames();
        assertEquals(lastNames, ImmutableList.of("Tarasenko"));
    }

    private static Date date(String textDate) {
        return fmt.parseDateTime(textDate).toDate();
    }
}
