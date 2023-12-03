package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.JditProperties;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.AdvancedPlayerSqlObject;
import com.github.arteam.jdit.domain.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DBIExtension.class)
@JditProperties("jdit-h2.properties")
public class SqlLocatorTest {

    @TestedSqlObject
    AdvancedPlayerSqlObject playerDao;

    @Test
    @DataSet("playerDao/players.sql")
    public void getPlayerBySeveralAtributes() {
        List<Player> players = playerDao.getPlayers(true, "John", true, "Tavares", true, "last_name", true,
                true, 3, true, 0);
        assertThat(players).hasSize(1);
        assertThat(players).extracting(Player::firstName).containsOnly("John");
        assertThat(players).extracting(Player::lastName).containsOnly("Tavares");
    }
}
