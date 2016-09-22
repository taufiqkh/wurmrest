package com.quiptiq.wurmrest.api;

import javax.annotation.concurrent.Immutable;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Encapsulates all information about one specific village
 */
@Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Village {
    private final int id;

    private final String name;
    private final Long deedId;
    private final String motto;
    private final String kingdom;
    private final Integer size;
    private final String founder;
    private final String mayor;
    private final String readableTimeUntilDisband;
    private final String disbander;
    private final Integer citizens;
    private final Integer allies;
    private final Integer guards;
    private final TilePosition tokenPosition;

    /**
     * Create a new Village with the given id and name
     * @param id Identifier for the village
     * @param name Name of the village, cannot be null
     * @throws IllegalArgumentException if the name is null
     */
    @JsonCreator
    public Village(@JsonProperty("id") int id,
                   @JsonProperty("name") @NotEmpty String name,
                   @JsonProperty("deedId") Long deedId,
                   @JsonProperty("motto") String motto,
                   @JsonProperty("kingdom") String kingdom,
                   @JsonProperty("size") Integer size,
                   @JsonProperty("founder") String founder,
                   @JsonProperty("mayor") String mayor,
                   @JsonProperty("readableTimeUntilDisband") String disbandTime,
                   @JsonProperty("disbander") String disbander,
                   @JsonProperty("citizens") Integer citizensCount,
                   @JsonProperty("allies") Integer allies,
                   @JsonProperty("guards") Integer guards,
                   @JsonProperty("tokenLocation") TilePosition tokenX
    ) {
        if (name == null) {
            throw new IllegalArgumentException("Village name cannot be null");
        }
        this.id = id;
        this.name = name;
        this.deedId = deedId;
        this.motto = motto;
        this.kingdom = kingdom;
        this.size = size;
        this.founder = founder;
        this.mayor = mayor;
        this.readableTimeUntilDisband = disbandTime;
        this.disbander = disbander;
        this.citizens = citizensCount;
        this.allies = allies;
        this.guards = guards;
        this.tokenPosition = tokenX;
    }

    public Village(int id, @NotEmpty String name) {
        this(id, name, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getDeedId() {
        return deedId;
    }

    public String getMotto() {
        return motto;
    }

    public String getKingdom() {
        return kingdom;
    }

    public Integer getSize() {
        return size;
    }

    public String getFounder() {
        return founder;
    }

    public String getMayor() {
        return mayor;
    }

    public String getReadableTimeUntilDisband() {
        return readableTimeUntilDisband;
    }

    public String getDisbander() {
        return disbander;
    }

    public Integer getCitizens() {
        return citizens;
    }

    public Integer getAllies() {
        return allies;
    }

    public Integer getGuards() {
        return guards;
    }

    public TilePosition getTokenPosition() {
        return tokenPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Village village = (Village) o;
        return id == village.id &&
                Objects.equals(name, village.name) &&
                Objects.equals(deedId, village.deedId) &&
                Objects.equals(motto, village.motto) &&
                Objects.equals(kingdom, village.kingdom) &&
                Objects.equals(size, village.size) &&
                Objects.equals(founder, village.founder) &&
                Objects.equals(mayor, village.mayor) &&
                Objects.equals(readableTimeUntilDisband, village.readableTimeUntilDisband) &&
                Objects.equals(disbander, village.disbander) &&
                Objects.equals(citizens, village.citizens) &&
                Objects.equals(allies, village.allies) &&
                Objects.equals(guards, village.guards) &&
                Objects.equals(tokenPosition, village.tokenPosition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, deedId, motto, kingdom, size, founder, mayor,
                readableTimeUntilDisband, disbander, citizens, allies, guards, tokenPosition);
    }
}
