package com.quiptiq.wurmrest.api;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the result of a call to check a bank balance.
 */
@Immutable
public class Balance {
    private final long balance;

    @JsonCreator
    public Balance(@JsonProperty("balance") long balance) {
        this.balance = balance;
    }

    @JsonProperty
    public long getBalance() {
        return balance;
    }
}
