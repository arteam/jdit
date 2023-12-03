package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import com.github.arteam.jdit.annotations.TestedDao;
import com.github.arteam.jdit.domain.PlayerDao;
import org.jdbi.v3.core.Handle;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractDbiDaoTest {

    @DBIHandle
    Handle handle;

    @TestedDao
    PlayerDao playerDao;

    @Test
    public void testInsert() throws Exception {
        Long playerId = playerDao.createPlayer("Vladimir", "Tarasenko",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("1991-08-05 00:00:00"),
                84, 99);
        assertThat(playerId).isPositive();
        String initials = handle.createQuery("select first_name || ' ' || last_name from players")
                .mapTo(String.class).one();
        assertThat(initials).isEqualTo("Vladimir Tarasenko");
    }
}
