package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DBIExtension.class)
@DataSet("/playerDao/getInitials.sql")
public class TestDataSetOnClassLevel {

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    public void testGetInitials() {
        List<String> lastNames = playerDao.getLastNames();
        assertThat(lastNames).containsOnly("Tarasenko");
    }

    @Test
    @DataSet("playerDao/players.sql")
    public void testGetInitialsForMultiplyPlayers(){
        List<String> lastNames = playerDao.getLastNames();
        assertThat(lastNames).containsExactly("Ellis", "Rattie", "Seguin", "Tarasenko", "Tavares");
    }

    @Test
    @DataSet("playerDao/players.sql")
    public void testGetAmountPlayersBornInYear() {
        int amount = playerDao.getAmountPlayersBornInYear(1991);
        assertThat(amount).isEqualTo( 2);
    }

    @Test
    @DataSet("playerDao/players.sql")
    public void testBornYearsForMultiplyPlayers() {
        Set<Integer> bornYears = playerDao.getBornYears();
        assertThat(bornYears).containsOnly(1990, 1991, 1992, 1993);
    }

    @Test
    public void testBornYearsForSinglePlayer() {
        Set<Integer> bornYears = playerDao.getBornYears();
        assertThat(bornYears).containsOnly(1991);
    }
}