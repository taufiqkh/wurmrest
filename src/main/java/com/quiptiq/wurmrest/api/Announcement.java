package com.quiptiq.wurmrest.api;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Server announcement
 */
@Immutable
public class Announcement {
    private final String message;

    @JsonCreator
    public Announcement(@JsonProperty("message") @NotEmpty String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
