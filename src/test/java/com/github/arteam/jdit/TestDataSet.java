package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
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
public class TestDataSet {

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testGetInitials() {
        List<String> lastNames = playerDao.getLastNames();
        Assert.assertEquals(lastNames, Arrays.asList("Tarasenko"));
    }
}