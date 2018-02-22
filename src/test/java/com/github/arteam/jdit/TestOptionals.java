package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import com.github.arteam.jdit.domain.entity.Player;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Optional;

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
        Assert.assertTrue(player.isPresent());
        Assert.assertEquals(player.get().firstName, "Vladimir");
        Assert.assertEquals(player.get().lastName, "Tarasenko");
    }

    @Test
    public void testNotExistOptional() {
        Optional<Player> player = playerSqlObject.findPlayer("Ryan", "Getzlaf");
        Assert.assertFalse(player.isPresent());
    }

    @Test
    public void testAbsentOptionalParameter(){
        List<Player> playersByWeight = playerSqlObject.getPlayersByWeight(Optional.empty());
        Assert.assertEquals(playersByWeight.size(), 1);
        Assert.assertFalse(playersByWeight.get(0).weight.isPresent());
    }
}
