package com.quiptiq.wurmrest.api;

import javax.annotation.concurrent.Immutable;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Encapsulates server status
 */
@Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerStatus {
    private final ZonedDateTime timeStamp;
    private final Boolean isRunning;
    private final boolean isConnected;

    @JsonCreator
    public ServerStatus(@JsonProperty("running") Boolean isRunning,
                        @JsonProperty("connected") boolean isConnected,
                        @JsonProperty("timeStamp") ZonedDateTime timeStamp) {
        this.isRunning = isRunning;
        this.isConnected = isConnected;
        this.timeStamp = timeStamp;
    }

    public Boolean isRunning() {
        return isRunning;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }
}
