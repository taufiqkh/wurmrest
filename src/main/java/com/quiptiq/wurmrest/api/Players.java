package com.quiptiq.wurmrest.api;

import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Collection of players and their metadata, containing a list of players and a total count.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Immutable
public final class Players {
    private static final CollectionsValidator validator = new CollectionsValidator();

    @JsonProperty("count")
    private final int count;

    @JsonProperty("players")
    private final List<String> players;

    @JsonCreator
    public Players(@JsonProperty("count") int count,
                   @JsonProperty("players") List<String>
            players) {
        this.players = validator.validatedImmutable(count, players);
        this.count = count;
    }

    public Players(int count) {
        this.count = count;
        this.players = null;
    }

    public Players(List<String> players) {
        this.players = validator.validatedImmutable(players);
        count = players.size();
    }

    public int getCount() {
        return count;
    }

    public List<String> getPlayers() {
        return players;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Players players1 = (Players) o;
        return count == players1.count &&
                Objects.equals(players, players1.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, players);
    }
}
