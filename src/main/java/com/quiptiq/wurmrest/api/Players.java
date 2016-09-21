package com.quiptiq.wurmrest.api;

import javax.annotation.concurrent.Immutable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Collection of players and their metadata, containing a list of players and a total count.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Immutable
public class Players {
    @JsonProperty("count")
    private final int count;

    @JsonProperty("players")
    private final List<String> players;

    @JsonCreator
    public Players(@JsonProperty("count") int count,
                   @JsonProperty("players") List<String>
            players) {
        this.count = count;
        if (players != null && players.size() != count) {
            throw new IllegalArgumentException("Count must equal the number of players");
        }
        this.players = players;
    }

    public Players(int count) {
        this.count = count;
        this.players = null;
    }

    public Players(List<String> players) {
        if (players == null) {
            throw new IllegalArgumentException("Players must be specified if using a list");
        }
        count = players.size();
        this.players = players;
    }

    public int getCount() {
        return count;
    }

    public List<String> getPlayers() {
        return players;
    }
}
