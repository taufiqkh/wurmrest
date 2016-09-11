package com.quiptiq.wurmrest.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Representation of a money transaction
 */
public class Transaction {
    private final long amount;
    private final String details;

    @JsonCreator
    public Transaction(@JsonProperty("amount") long amount,
                       @JsonProperty("details") @NotEmpty String details) {
        this.amount = amount;
        this.details = details;
    }

    @JsonProperty
    public long getAmount() {
        return amount;
    }

    @JsonProperty
    public String getDetails() {
        return details;
    }
}
