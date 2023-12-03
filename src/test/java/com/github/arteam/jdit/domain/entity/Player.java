package com.github.arteam.jdit.domain.entity;

import java.util.Date;
import java.util.Optional;

/**
 * Date: 2/7/15
 * Time: 5:00 PM
 *
 * @author Artem Prigoda
 */
public record Player(Optional<Long> id, String firstName, String lastName, Date birthDate, Optional<Integer> height,
                     Optional<Integer> weight) {
    public static Player of(String firstName, String lastName, Date birthDate, int height, int weight) {
        return new Player(Optional.empty(), firstName, lastName, birthDate, Optional.of(height), Optional.of(weight));
    }
}
