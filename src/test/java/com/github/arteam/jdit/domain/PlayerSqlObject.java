package com.github.arteam.jdit.domain;

import com.github.arteam.jdit.domain.entity.Player;
import com.google.common.collect.ImmutableSet;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.*;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.joda.time.DateTime;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Date: 1/25/15
 * Time: 11:26 PM
 *
 * @author Artem Prigoda
 */
@RegisterRowMapper(PlayerSqlObject.PlayerMapper.class)
public interface PlayerSqlObject {

    @GetGeneratedKeys
    @SqlUpdate("insert into players(first_name, last_name, birth_date, weight, height) values" +
            "(:first_name, :last_name, :birth_date, :weight, :height)")
    Long createPlayer(@Bind("first_name") String firstName, @Bind("last_name") String lastName,
                      @Bind("birth_date") Date birthDate, @Bind("height") int height,
                      @Bind("weight") int weight);

    @GetGeneratedKeys
    @SqlUpdate("insert into players(first_name, last_name, birth_date, weight, height) values" +
            "(:first_name, :last_name, :birth_date, :weight, :height)")
    Long createPlayer(@PlayerBinder Player player);

    @SqlQuery("select last_name from players order by last_name")
    List<String> getLastNames();

    @SqlQuery("select count(*) from players where extract(year from birth_date) = :year")
    int getAmountPlayersBornInYear(@Bind("year") int year);

    @SqlQuery("select * from players where birth_date > :date")
    List<Player> getPlayersBornAfter(@Bind("date") DateTime date);

    @SqlQuery("select birth_date from players where first_name=:first_name and last_name=:last_name")
    DateTime getPlayerBirthDate(@Bind("first_name") String firstName, @Bind("last_name") String lastName);

    @SqlQuery("select distinct extract(year from birth_date) player_year from players order by player_year")
    Set<Integer> getBornYears();

    @SqlQuery("select * from players where first_name=:first_name and last_name=:last_name")
    Optional<Player> findPlayer(@Bind("first_name") String firstName, @Bind("last_name") String lastName);

    @SqlQuery("select * from players where weight <> :weight")
    List<Player> getPlayersByWeight(@Bind("weight") Optional<Integer> weight);

    @SqlQuery("select first_name from players")
    ImmutableSet<String> getFirstNames();

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER})
    @SqlStatementCustomizingAnnotation(PlayerBinder.Factory.class)
    @interface PlayerBinder {

        class Factory implements SqlStatementCustomizerFactory {

            @Override
            public SqlStatementParameterCustomizer createForParameter(Annotation annotation, Class<?> sqlObjectType,
                                                                      Method method, Parameter param, int index,
                                                                      Type type) {
                return (stmt, arg) -> {
                    Player p = (Player) arg;
                    stmt.bind("first_name", p.firstName);
                    stmt.bind("last_name", p.lastName);
                    stmt.bind("birth_date", p.birthDate);
                    stmt.bind("weight", p.weight);
                    stmt.bind("height", p.height);
                };
            }
        }
    }

    class PlayerMapper implements RowMapper<Player> {
        @Override
        public Player map(ResultSet r, StatementContext ctx) throws SQLException {
            int height = r.getInt("height");
            int weight = r.getInt("weight");
            return new Player(Optional.of(r.getLong("id")), r.getString("first_name"), r.getString("last_name"),
                    r.getTimestamp("birth_date"),
                    height != 0 ? Optional.of(height) : Optional.empty(),
                    weight != 0 ? Optional.of(weight) : Optional.empty());
        }
    }
}
