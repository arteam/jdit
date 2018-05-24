package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import com.github.arteam.jdit.annotations.TestedDao;
import com.github.arteam.jdit.domain.PlayerDaoWithConstructor;
import org.jdbi.v3.core.Handle;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DBIRunner.class)
public class DBIDaoWithConstructorTest {

    @DBIHandle
    Handle handle;

    @TestedDao
    PlayerDaoWithConstructor playerDao;

    @Test
    public void testInsert() throws Exception {
        Long playerId = playerDao.createPlayer("Vladimir", "Tarasenko", new SimpleDateFormat("yyyy-MM-dd HH:mm:SS")
                .parse("1991-08-05 00:00:00"), 84, 99);
        assertThat(playerId).isPositive();
        assertThat(handle.createQuery("select first_name || ' ' || last_name from players")
                .mapTo(String.class)
                .findOnly()).isEqualTo("Vladimir Tarasenko");
    }

    @Test
    public void testGetInitials() {
        assertThat(playerDao.getLastNames()).isEmpty();
    }
}