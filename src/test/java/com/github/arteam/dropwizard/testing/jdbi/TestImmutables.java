package com.github.arteam.dropwizard.testing.jdbi;

import com.github.arteam.dropwizard.testing.jdbi.annotations.DataSet;
import com.github.arteam.dropwizard.testing.jdbi.annotations.TestedSqlObject;
import com.github.arteam.dropwizard.testing.jdbi.domain.PlayerSqlObject;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Date: 2/12/15
 * Time: 12:10 AM
 *
 * @author Artem Prigoda
 */
@DataSet("playerDao/players.sql")
@RunWith(DBIRunner.class)
public class TestImmutables {

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    public void testSet(){
        ImmutableSet<String> firstNames = playerDao.getFirstNames();
        System.out.println(firstNames);
        Assert.assertEquals(ImmutableSet.of("Vladimir", "Tyler", "Ryan", "John", "Ty"), firstNames);
    }
}
