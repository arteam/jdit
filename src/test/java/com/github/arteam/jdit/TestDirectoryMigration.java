package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.JditProperties;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.TeamSqlObject;
import com.github.arteam.jdit.domain.entity.Player;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Date: 2/22/15
 * Time: 6:16 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
@JditProperties("jdit-schema-directory.properties")
public class TestDirectoryMigration {

    @TestedSqlObject
    TeamSqlObject teamSqlObject;

    @Test
    @DataSet("teamDao/teams-and-players.sql")
    public void testGetTeamPlayers() {
        List<Player> players = teamSqlObject.getPlayers("St. Louis Blues");
        for (Player player : players) {
            System.out.println(player);
        }
        Assert.assertEquals(players.get(0).lastName, "Allen");
        Assert.assertEquals(players.get(1).lastName, "Schwartz");
        Assert.assertEquals(players.get(2).lastName, "Tarasenko");
    }
}
