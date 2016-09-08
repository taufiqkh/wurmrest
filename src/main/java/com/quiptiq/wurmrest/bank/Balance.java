package com.quiptiq.wurmrest.bank;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quiptiq.wurmrest.WurmConstants;
import org.hibernate.validator.constraints.Length;

/**
 * Balance at a given point in time.
 */
public final class Balance {
    private LocalDateTime timeStamp;

    @Length(max = WurmConstants.MAX_NAME_LENGTH)
    private String player;

    private int money;

    public Balance() {
        // Jackson deserialisation
    }
    public Balance(String player, int money) {
        timeStamp = LocalDateTime.now();
        this.player = player;
        this.money = money;
    }

    /**
     * @return Local date/time at which this balance was taken.
     */
    @JsonProperty
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    /**
     * @return Name of the player to which this balance belongs.
     */
    @JsonProperty
    public String getPlayer() {
        return player;
    }

    /**
     * @return The amount of money the player has in the bank.
     */
    @JsonProperty
    public int getMoney() {
        return money;
    }
}
