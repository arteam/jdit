package com.github.arteam.jdit.domain;

import org.jdbi.v3.core.Jdbi;

/**
 * Date: 1/25/15
 * Time: 6:33 PM
 *
 * @author Artem Prigoda
 */
public class PlayerDaoWithConstructor extends BasePlayerDao {

    private Jdbi dbi;

    public PlayerDaoWithConstructor(Jdbi dbi) {
        this.dbi = dbi;
    }

    @Override
    public Jdbi dbi() {
        return dbi;
    }
}
