package com.github.arteam.jdit.domain.entity;

import com.google.common.base.MoreObjects;

import java.util.Date;
import java.util.Optional;

/**
 * Date: 2/7/15
 * Time: 5:00 PM
 *
 * @author Artem Prigoda
 */
public class Player {

    public final Optional<Long> id;
    public final String firstName;
    public final String lastName;
    public final Date birthDate;
    public final Optional<Integer> height;
    public final Optional<Integer> weight;

    public Player(Optional<Long> id, String firstName, String lastName, Date birthDate,
                  Optional<Integer> height, Optional<Integer> weight) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.height = height;
        this.weight = weight;
    }

    public Player(String firstName, String lastName, Date birthDate, int height, int weight) {
        this(Optional.<Long>empty(), firstName, lastName, birthDate, Optional.of(height), Optional.of(weight));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id.orElse(null))
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("birthDate", birthDate)
                .add("height", height.orElse(null))
                .add("weight", weight.orElse(null))
                .toString();
    }
}
