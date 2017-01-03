package com.github.arteam.jdit.domain;

import com.github.arteam.jdit.domain.entity.Player;
import com.google.common.base.Optional;
import org.jdbi.v3.core.StatementContext;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.sqlobject.Bind;
import org.jdbi.v3.sqlobject.SqlQuery;
import org.jdbi.v3.sqlobject.customizers.Define;
import org.jdbi.v3.sqlobject.customizers.RegisterRowMapper;
import org.jdbi.v3.stringtemplate.UseStringTemplateSqlLocator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
            return new Player(Optional.of(r.getLong("id")), r.getString("first_name"), r.getString("last_name"),
                    r.getTimestamp("birth_date"),
                    height != 0 ? Optional.of(height) : Optional.absent(),
                    weight != 0 ? Optional.of(weight) : Optional.absent());
        }
    }
}
