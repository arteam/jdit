package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Date: 2/12/15
 * Time: 12:10 AM
 *
 * @author Artem Prigoda
 */
@DataSet("playerDao/players.sql")
@RunWith(DBIRunner.class)
public class TestImmutables {

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    public void testSet() {
        assertThat(playerDao.getFirstNames()).containsOnly("Vladimir", "Tyler", "Ryan", "John", "Ty");
    }
}
