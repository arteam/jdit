package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import com.github.arteam.jdit.annotations.TestedDao;
import com.github.arteam.jdit.domain.PlayerDaoWithConstructor;
import org.jdbi.v3.core.Handle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.text.SimpleDateFormat;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DBIExtension.class)
public class DBIDaoWithConstructorTest {

    @DBIHandle
    Handle handle;

    @TestedDao
    PlayerDaoWithConstructor playerDao;

    @Test
    public void testInsert() throws Exception {
        Long playerId = playerDao.createPlayer("Vladimir", "Tarasenko", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .parse("1991-08-05 00:00:00"), 84, 99);
        assertThat(playerId).isPositive();
        assertThat(handle.createQuery("select first_name || ' ' || last_name from players")
                .mapTo(String.class).one()).isEqualTo("Vladimir Tarasenko");
    }

    @Test
    public void testGetInitials() {
        assertThat(playerDao.getLastNames()).isEmpty();
    }
}