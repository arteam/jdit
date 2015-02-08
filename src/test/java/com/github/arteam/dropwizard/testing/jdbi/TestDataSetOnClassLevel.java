package com.github.arteam.dropwizard.testing.jdbi;

import com.github.arteam.dropwizard.testing.jdbi.annotations.DataSet;
import com.github.arteam.dropwizard.testing.jdbi.annotations.TestedSqlObject;
import com.github.arteam.dropwizard.testing.jdbi.domain.PlayerSqlObject;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Date: 1/22/15
 * Time: 8:57 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
@DataSet("/playerDao/getInitials.sql")
public class TestDataSetOnClassLevel {

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    public void testGetInitials() {
        List<String> lastNames = playerDao.getLastNames();
        System.out.println(lastNames);
        Assert.assertEquals(lastNames, Arrays.asList("Tarasenko"));
    }

    @Test
    @DataSet("playerDao/players.sql")
    public void testGetInitialsForMultiplyPlayers(){
        List<String> lastNames = playerDao.getLastNames();
        System.out.println(lastNames);
        Assert.assertEquals(lastNames, Arrays.asList("Ellis", "Rattie", "Seguin", "Tarasenko", "Tavares"));
    }

    @Test
    @DataSet("playerDao/players.sql")
    public void testGetAmountPlayersBornInYear() {
        int amount = playerDao.getAmountPlayersBornInYear(1991);
        Assert.assertEquals(amount, 2);
    }

    @Test
    @DataSet("playerDao/players.sql")
    public void testBornYearsForMultiplyPlayers() {
        Set<Integer> bornYears = playerDao.getBornYears();
        System.out.println(bornYears);
        Assert.assertEquals(bornYears, ImmutableSet.of(1990, 1991, 1992, 1993));
    }

    @Test
    public void testBornYearsForSinglePlayer() {
        Set<Integer> bornYears = playerDao.getBornYears();
        System.out.println(bornYears);
        Assert.assertEquals(bornYears, ImmutableSet.of(1991));
    }
}