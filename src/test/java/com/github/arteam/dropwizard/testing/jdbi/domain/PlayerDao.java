package com.github.arteam.dropwizard.testing.jdbi.domain;

import org.skife.jdbi.v2.DBI;

/**
 * Date: 1/25/15
 * Time: 6:33 PM
 *
 * @author Artem Prigoda
 */
public class PlayerDao extends BasePlayerDao {

    private DBI dbi;

    @Override
    public DBI dbi() {
        return dbi;
    }
}
