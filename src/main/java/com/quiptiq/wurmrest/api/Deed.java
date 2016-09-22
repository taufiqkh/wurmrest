package com.quiptiq.wurmrest.api;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Encapsulates all information about one specific deed
 */
@Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Deed {
    private final int id;

    private final String name;

    /**
     * Create a new Deed with the given id and name
     * @param id Identifier for the deed
     * @param name Name of the deed, cannot be null
     * @throws IllegalArgumentException if the name is null
     */
    @JsonCreator
    public Deed(@JsonProperty("id") int id, @JsonProperty("name") @NotEmpty String name) {
        if (name == null) {
            throw new IllegalArgumentException("Deed name cannot be null");
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
        if (other == null || !(other instanceof Deed)) {
            return false;
        }
        Deed deed = (Deed) other;
        return this.id == deed.id && this.name.equals(deed.name);
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + id;
    }
}
