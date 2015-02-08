package com.github.arteam.dropwizard.testing.jdbi;

import com.github.arteam.dropwizard.testing.jdbi.annotations.DataSet;
import com.github.arteam.dropwizard.testing.jdbi.annotations.TestedSqlObject;
import com.github.arteam.dropwizard.testing.jdbi.domain.PlayerSqlObject;
import com.github.arteam.dropwizard.testing.jdbi.domain.entity.Player;
import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Date: 2/7/15
 * Time: 5:39 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
@DataSet("playerDao/players.sql")
public class TestOptionals {

    @TestedSqlObject
    PlayerSqlObject playerSqlObject;

    @Test
    public void testExistOptional() {
        Optional<Player> player = playerSqlObject.findPlayer("Vladimir", "Tarasenko");
        System.out.println(player);
        Assert.assertTrue(player.isPresent());
        Assert.assertEquals(player.get().firstName, "Vladimir");
        Assert.assertEquals(player.get().lastName, "Tarasenko");
    }

    @Test
    public void testNotExistOptional() {
        Optional<Player> player = playerSqlObject.findPlayer("Ryan", "Getzlaf");
        System.out.println(player);
        Assert.assertFalse(player.isPresent());
    }

    @Test
    public void testAbsentOptionalParameter(){
        List<Player> playersByWeight = playerSqlObject.getPlayersByWeight(Optional.<Integer>absent());
        System.out.println(playersByWeight);
        Assert.assertEquals(playersByWeight.size(), 1);
        Assert.assertFalse(playersByWeight.get(0).weight.isPresent());
    }
}
