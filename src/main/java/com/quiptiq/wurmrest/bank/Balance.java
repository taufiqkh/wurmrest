package com.quiptiq.wurmrest.bank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the result of a call to check a bank balance.
 */
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
