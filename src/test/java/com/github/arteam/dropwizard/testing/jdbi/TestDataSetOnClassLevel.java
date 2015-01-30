package com.github.arteam.dropwizard.testing.jdbi;

import com.github.arteam.dropwizard.testing.jdbi.annotations.DataSet;
import com.github.arteam.dropwizard.testing.jdbi.annotations.TestedSqlObject;
import com.github.arteam.dropwizard.testing.jdbi.domain.PlayerSqlObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

/**
 * Date: 1/22/15
 * Time: 8:57 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
@DataSet("playerDao/getInitials.sql")
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
    @DataSet("playerDao/getAmountPlayersBornInYear.sql")
    public void testGetAmountPlayersBornInYear(){
        List<Integer> bornYears = playerDao.getBornYears();
        System.out.println("Born years: " + bornYears);

        int amount = playerDao.getAmountPlayersBornInYear(1991);
        Assert.assertEquals(amount, 2);
    }
}