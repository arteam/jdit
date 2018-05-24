package com.github.arteam.jdit;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DBIRunner.class)
public class DBIDaoDbiDaoTest extends AbstractDbiDaoTest {

    @Test
    public void testGetInitials() {
        assertThat(playerDao.getLastNames()).isEmpty();
    }
}