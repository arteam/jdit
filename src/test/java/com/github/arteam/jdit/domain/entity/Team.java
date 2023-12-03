package com.github.arteam.jdit.domain.entity;

import java.util.Optional;

/**
 * Date: 2/7/15
 * Time: 4:58 PM
 *
 * @author Artem Prigoda
 */
public record Team(Optional<Long> id, String name, Division division) {

    public static Team of(String name, Division division) {
        return new Team(Optional.empty(), name, division);
    }
}
