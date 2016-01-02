package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.JditProperties;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import com.google.common.collect.ImmutableList;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skife.jdbi.v2.Handle;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Date: 1/1/16
 * Time: 7:51 PM
 *
 * @author Artem Prigoda
 */
@JditProperties("jdit-mysql.properties")
public class MySqlDbTest extends AlternateDatabaseTest {
}
