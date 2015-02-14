package com.github.arteam.jdit.domain;

import com.github.arteam.jdit.domain.entity.Division;
import com.github.arteam.jdit.domain.entity.Player;
import com.github.arteam.jdit.domain.entity.Team;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Date: 2/7/15
 * Time: 4:57 PM
 *
 * @author Artem Prigoda
 */
@RegisterMapper(PlayerSqlObject.PlayerMapper.class)
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

}
