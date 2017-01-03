package com.github.arteam.jdit.domain;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.Date;
import java.util.List;

/**
 * Date: 1/27/15
 * Time: 11:03 PM
 *
 * @author Artem Prigoda
 */
public abstract class BasePlayerDao {

    public abstract Jdbi dbi();

    public Long createPlayer(String firstName, String lastName, Date birthDate, int height, int weight) {
        try (Handle handle = dbi().open()) {
            return handle.createUpdate("insert into players(first_name, last_name, birth_date, weight, height) values" +
                    "(:first_name, :last_name, :birth_date, :weight, :height)")
                    .bind("first_name", firstName)
                    .bind("last_name", lastName)
                    .bind("birth_date", birthDate)
                    .bind("height", height)
                    .bind("weight", weight)
                    .executeAndReturnGeneratedKeys(Long.class)
                    .findOnly();
        }
    }

    public List<String> getLastNames() {
        try (Handle handle = dbi().open()) {
            return handle.createQuery("select last_name from players")
                    .mapTo(String.class)
                    .list();
        }
    }
}
