package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.JditProperties;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.TeamSqlObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DBIRunner.class)
@JditProperties("jdit-schema-directory.properties")
public class TestDirectoryMigration {

    @TestedSqlObject
    TeamSqlObject teamSqlObject;

    @Test
    @DataSet({"teamDao/insert-divisions.sql", "teamDao/teams-and-players.sql"})
    public void testGetTeamPlayers() {
        assertThat(teamSqlObject.getPlayers("St. Louis Blues")).hasSize(3)
                .extracting(p -> p.lastName)
                .containsExactly("Allen", "Schwartz", "Tarasenko");
    }
}
