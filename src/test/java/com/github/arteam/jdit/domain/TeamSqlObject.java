package com.github.arteam.jdit.domain;

import com.github.arteam.jdit.domain.entity.Division;
import com.github.arteam.jdit.domain.entity.Player;
import com.github.arteam.jdit.domain.entity.Team;
import org.jdbi.v3.sqlobject.CreateSqlObject;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;

/**
 * Date: 2/7/15
 * Time: 4:57 PM
 *
 * @author Artem Prigoda
 */
@RegisterRowMapper(PlayerSqlObject.PlayerMapper.class)
public interface TeamSqlObject {

    @CreateSqlObject
    PlayerSqlObject playerDao();

    @SqlUpdate("insert into teams(name, division) values (:name, :division)")
    @GetGeneratedKeys
    long createTeam(@Bind("name") String name, @Bind("division") Division division);

    @SqlUpdate("insert into roster(team_id, player_id) values (:team_id, :player_id)")
    void addPlayerToTeam(@Bind("team_id") long teamId, @Bind("player_id") long playerId);

    @Transaction
    default void addTeam(Team team, List<Player> players) {
        long teamId = createTeam(team.name, team.division);
        for (Player player : players) {
            long playerId = playerDao().createPlayer(player);
            addPlayerToTeam(teamId, playerId);
        }
    }

    @SqlQuery("select * from players p " +
            "inner join roster r on r.player_id=p.id " +
            "inner join teams t on r.team_id=t.id " +
            "where t.name = :team_name " +
            "order by p.last_name")
    List<Player> getPlayers(@Bind("team_name") String teamName);
}
