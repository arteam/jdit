package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import com.github.arteam.jdit.domain.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DBIExtension.class)
@DataSet("playerDao/players.sql")
public class TestOptionals {

    @TestedSqlObject
    PlayerSqlObject playerSqlObject;

    @Test
    public void testExistOptional() {
        Optional<Player> player = playerSqlObject.findPlayer("Vladimir", "Tarasenko");
        assertThat(player).map(Player::firstName).contains("Vladimir");
        assertThat(player).map(Player::lastName).contains("Tarasenko");
    }

    @Test
    public void testNotExistOptional() {
        Optional<Player> player = playerSqlObject.findPlayer("Ryan", "Getzlaf");
        assertThat(player).isNotPresent();
    }
}
