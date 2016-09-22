package com.quiptiq.wurmrest.api;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Encapsulates all information about one specific village
 */
@Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Village {
    private final int id;

    private final String name;

    /**
     * Create a new Village with the given id and name
     * @param id Identifier for the village
     * @param name Name of the village, cannot be null
     * @throws IllegalArgumentException if the name is null
     */
    @JsonCreator
    public Village(@JsonProperty("id") int id, @JsonProperty("name") @NotEmpty String name) {
        if (name == null) {
            throw new IllegalArgumentException("Village name cannot be null");
        }
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Village)) {
            return false;
        }
        Village village = (Village) other;
        return this.id == village.id && this.name.equals(village.name);
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + id;
    }
}
