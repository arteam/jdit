package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DBIHandle;
import org.jdbi.v3.core.Handle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DBIExtension.class)
public class HandleTest {

    @DBIHandle
    Handle handle;

    @Test
    public void testInsert() {
        int amount = handle.execute("insert into players(first_name, last_name, birth_date, weight, height)" +
                " values ('Vladimir','Tarasenko', '1991-08-05', 84, 99)");
        assertThat(amount).isEqualTo(1);

        String initials = handle.createQuery("select first_name || ' ' || last_name from players")
                .mapTo(String.class).one();
        assertThat(initials).isEqualTo("Vladimir Tarasenko");
    }

    @Test
    public void testGetInitials() {
        assertThat(handle.createQuery("select last_name from players")
                .mapTo(String.class)
                .list()).isEmpty();
    }
}
