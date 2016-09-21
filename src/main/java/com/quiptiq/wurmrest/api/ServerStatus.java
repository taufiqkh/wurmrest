package com.quiptiq.wurmrest.api;

import javax.annotation.concurrent.Immutable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Encapsulates server status
 */
@Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerStatus {
    private final LocalDateTime timeStamp;
    private final Boolean isRunning;
    private final boolean isConnectable;

    @JsonCreator
    public ServerStatus(@JsonProperty("isRunning") Boolean isRunning,
                        @JsonProperty("isConnectable") boolean isConnectable,
                        @JsonProperty("timeStamp") LocalDateTime timeStamp) {
        this.isRunning = isRunning;
        this.isConnectable = isConnectable;
        this.timeStamp = timeStamp;
    }

    public Boolean getIsRunning() {
        return isRunning;
    }

    public boolean isConnectable() {
        return isConnectable;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
}
