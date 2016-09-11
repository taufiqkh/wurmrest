package com.quiptiq.wurmrest.api;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Shutdown command, optionally with a timer and instigator.
 */
@Immutable
public class ShutdownCommand {
    private final String instigator;
    private final Integer timer;
    private final String reason;

    @JsonCreator
    public ShutdownCommand(@JsonProperty("instigator") @NotEmpty String instigator,
                           @JsonProperty("timer") Integer timer,
                           @JsonProperty("reason") @NotEmpty String reason) {
        this.instigator = instigator;
        this.timer = timer;
        this.reason = reason;
    }

    public String getInstigator() {
        return instigator;
    }

    public Integer getTimer() {
        return timer;
    }

    public String getReason() {
        return reason;
    }
}
