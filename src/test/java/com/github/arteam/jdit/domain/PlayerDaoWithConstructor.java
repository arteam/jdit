package com.github.arteam.jdit.domain;

import org.skife.jdbi.v2.DBI;

/**
 * Date: 1/25/15
 * Time: 6:33 PM
 *
 * @author Artem Prigoda
 */
public class PlayerDaoWithConstructor extends BasePlayerDao {

    private DBI dbi;

    public PlayerDaoWithConstructor(DBI dbi) {
        this.dbi = dbi;
    }

    @Override
    public DBI dbi() {
        return dbi;
    }
}
