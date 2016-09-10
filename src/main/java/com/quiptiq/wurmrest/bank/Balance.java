package com.quiptiq.wurmrest.bank;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the result of a call to check a bank balance.
 */
public class Balance {
    private final long balance;

    public Balance(long balance) {
        this.balance = balance;
    }

    @JsonProperty
    public long getBalance() {
        return balance;
    }
}
