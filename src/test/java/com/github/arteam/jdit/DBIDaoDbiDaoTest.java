package com.github.arteam.jdit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DBIExtension.class)
public class DBIDaoDbiDaoTest extends AbstractDbiDaoTest {

    @Test
    public void testGetInitials() {
        assertThat(playerDao.getLastNames()).isEmpty();
    }
}