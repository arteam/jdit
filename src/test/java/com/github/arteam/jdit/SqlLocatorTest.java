package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.JditProperties;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.AdvancedPlayerSqlObject;
import com.github.arteam.jdit.domain.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Artem Prigoda
 * @since 18.07.16
 */
@RunWith(DBIRunner.class)
@JditProperties("jdit-h2-pgs.properties")
public class SqlLocatorTest {

    @TestedSqlObject
    AdvancedPlayerSqlObject playerDao;

    @Test
    @DataSet("playerDao/players.sql")
    public void getPlayerBySeveralAtributes() {
        List<Player> players = playerDao.getPlayers(true, "John", true, "Tavares", true, "last_name", true,
                true, 3, true, 0);
        assertEquals(players.size(), 1);
        assertEquals(players.get(0).firstName, "John");
        assertEquals(players.get(0).lastName, "Tavares");
    }
}
