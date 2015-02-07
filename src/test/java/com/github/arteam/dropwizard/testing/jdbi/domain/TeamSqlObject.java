package com.github.arteam.dropwizard.testing.jdbi.domain;

import com.github.arteam.dropwizard.testing.jdbi.domain.entity.Division;
import com.github.arteam.dropwizard.testing.jdbi.domain.entity.Player;
import com.github.arteam.dropwizard.testing.jdbi.domain.entity.Team;
import com.google.common.base.Optional;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Date: 2/7/15
 * Time: 4:57 PM
 *
 * @author Artem Prigoda
 */
@RegisterMapper(TeamSqlObject.PlayerMapper.class)
public abstract class TeamSqlObject {

    @CreateSqlObject
    abstract PlayerSqlObject playerDao();

    @SqlUpdate("insert into teams(name, division) values (:name, :division)")
    @GetGeneratedKeys
    abstract long createTeam(@Bind("name") String name, @Bind("division") Division division);

    @SqlUpdate("insert into roster(team_id, player_id) values (:team_id, :player_id)")
    abstract long addPlayerToTeam(@Bind("team_id") long teamId, @Bind("player_id") long playerId);

    @Transaction
    public void addTeam(Team team, List<Player> players) {
        long teamId = createTeam(team.name, team.division);
        for (Player player : players) {
            Long playerId = playerDao().createPlayer(player);
            addPlayerToTeam(teamId, playerId);
        }
    }

    @SqlQuery("select * from players p " +
            "inner join roster r on r.player_id=p.id " +
            "inner join teams t on r.team_id=t.id " +
            "where t.name = :team_name")
    public abstract List<Player> getPlayers(@Bind("team_name") String teamName);

    public static class PlayerMapper implements ResultSetMapper<Player> {
        @Override
        public Player map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new Player(Optional.of(r.getLong("id")), r.getString("first_name"), r.getString("last_name"),
                    r.getTimestamp("birth_date"), r.getInt("height"), r.getInt("weight"));
        }
    }
}
