package com.github.arteam.jdit;

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

import java.util.List;

/**
 * Date: 1/1/16
 * Time: 7:51 PM
 *
 * @author Artem Prigoda
 */
@JditProperties("jdit-hsqldb-pgs.properties")
@RunWith(DBIRunner.class)
public class HsqlDbTest {

    private static final DateTimeFormatter fmt = ISODateTimeFormat.date().withZoneUTC();

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testCreatePlayer() {
        Long playerId = playerDao.createPlayer("Colton", "Parayko", fmt.parseDateTime("1993-05-12").toDate(),
                196, 102);
        Assert.assertEquals(playerId.longValue(), 2L);
    }

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testHetLastNames() {
        List<String> lastNames = playerDao.getLastNames();
        Assert.assertEquals(lastNames, ImmutableList.of("Tarasenko"));
    }

}
