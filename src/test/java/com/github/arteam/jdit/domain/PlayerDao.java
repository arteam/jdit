package com.github.arteam.jdit.domain;

import org.jdbi.v3.core.Jdbi;

/**
 * Date: 1/25/15
 * Time: 6:33 PM
 *
 * @author Artem Prigoda
 */
public class PlayerDao extends BasePlayerDao {

    private Jdbi dbi;

    @Override
    public Jdbi dbi() {
        return dbi;
    }
}
