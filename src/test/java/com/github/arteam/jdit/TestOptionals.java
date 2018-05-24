package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import com.github.arteam.jdit.domain.entity.Player;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DBIRunner.class)
@DataSet("playerDao/players.sql")
public class TestOptionals {

    @TestedSqlObject
    PlayerSqlObject playerSqlObject;

    @Test
    public void testExistOptional() {
        Optional<Player> player = playerSqlObject.findPlayer("Vladimir", "Tarasenko");
        assertThat(player).map(p -> p.firstName).contains("Vladimir");
        assertThat(player).map(p -> p.lastName).contains("Tarasenko");
    }

    @Test
    public void testNotExistOptional() {
        Optional<Player> player = playerSqlObject.findPlayer("Ryan", "Getzlaf");
        assertThat(player).isNotPresent();
    }
}
