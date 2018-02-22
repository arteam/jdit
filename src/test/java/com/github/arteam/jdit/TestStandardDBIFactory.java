package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import com.github.arteam.jdit.annotations.JditProperties;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import org.jdbi.v3.core.Handle;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Date: 5/5/15
 * Time: 11:21 PM
 *
 * @author Artem Prigoda
 */
@JditProperties("jdit-alternate-factory.properties")
@RunWith(DBIRunner.class)
public class TestStandardDBIFactory {

    @DBIHandle
    Handle handle;

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    public void testInsert() throws Exception {
        playerDao.createPlayer("Petteri", "Lindbohm",
                new SimpleDateFormat("yyyy-MM-dd").parse("1993-09-23"), 191, 95);
        assertThat(handle.createQuery("select (first_name || ' ' || last_name) initials from players")
                .mapToMap()
                .findOnly()
                .get("initials")).isEqualTo("Petteri Lindbohm");
    }
}
