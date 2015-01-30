package com.github.arteam.dropwizard.testing.jdbi.domain;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Date: 1/25/15
 * Time: 11:26 PM
 *
 * @author Artem Prigoda
 */
public interface PlayerSqlObject {

    @GetGeneratedKeys
    @SqlUpdate("insert into players(first_name, last_name, birth_date, weight, height) values" +
            "(:first_name, :last_name, :birth_date, :weight, :height)")
    Long createPlayer(@Bind("first_name") String firstName, @Bind("last_name") String lastName,
                      @Bind("birth_date") Date birthDate, @Bind("height") int height,
                      @Bind("weight") int weight);

    @SqlQuery("select last_name from players order by last_name")
    List<String> getLastNames();

    @SqlQuery("select count(*) from players where year(birth_date) = :year")
    int getAmountPlayersBornInYear(@Bind("year") int year);

    @SqlQuery("select distinct year(birth_date) player_year from players order by player_year")
    public Set<Integer> getBornYears();
}
