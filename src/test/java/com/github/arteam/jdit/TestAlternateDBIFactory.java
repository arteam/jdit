package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import com.github.arteam.jdit.annotations.JditProperties;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import org.jdbi.v3.core.Handle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.text.SimpleDateFormat;

import static org.assertj.core.api.Assertions.assertThat;

@JditProperties("jdit-alternate-factory.properties")
@ExtendWith(DBIExtension.class)
public class TestAlternateDBIFactory {

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
                .one()
                .get("initials")).isEqualTo("Petteri Lindbohm");
    }
}
