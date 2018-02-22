package com.github.arteam.jdit;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Date: 1/22/15
 * Time: 8:57 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
public class DBIDaoDbiDaoTest extends AbstractDbiDaoTest {

    @Test
    public void testGetInitials() {
        assertThat(playerDao.getLastNames()).isEmpty();
    }
}