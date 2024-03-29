package com.github.arteam.jdit.domain;

import com.github.arteam.jdit.domain.entity.Player;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.stringtemplate4.UseStringTemplateSqlLocator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Date: 1/25/15
 * Time: 11:26 PM
 *
 * @author Artem Prigoda
 */
@RegisterRowMapper(AdvancedPlayerSqlObject.PlayerMapper.class)
@UseStringTemplateSqlLocator
public interface AdvancedPlayerSqlObject {

    @SqlQuery
    List<Player> getPlayers(@Define("isFirstName") boolean isFirstName, @Bind("firstName") String firstName,
                            @Define("isFirstName") boolean isLastName, @Bind("lastName") String lastName,
                            @Define("isSort") boolean isSort, @Define("sortBy") String sortBy,
                            @Define("isSortDesc") boolean isSortDesc,
                            @Define("isLimit") boolean isLimit, @Define("limit") int limit,
                            @Define("isOffset") boolean isOffset, @Define("offset") int offset);

    class PlayerMapper implements RowMapper<Player> {
        @Override
        public Player map(ResultSet r, StatementContext ctx) throws SQLException {
            int height = r.getInt("height");
            int weight = r.getInt("weight");
            return new Player(Optional.of(r.getLong("id")), r.getString("first_name"),
                    r.getString("last_name"), r.getTimestamp("birth_date"),
                    height != 0 ? Optional.of(height) : Optional.empty(),
                    weight != 0 ? Optional.of(weight) : Optional.empty());
        }
    }
}
