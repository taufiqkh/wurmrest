package com.quiptiq.wurmrest.bank;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the result of a call to check a bank balance.
 */
public class BalanceResult {
    public enum BalanceResultType {
        OK(0),
        BAD_PLAYER_ID(1),
        NEGATIVE_BALANCE(2),
        UNKNOWN(-1);

        private final int value;
        BalanceResultType(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    public static int BALANCE_ERROR = -1;
    private final BalanceResultType errorType;
    private final String errorMessage;
    private final long balance;
    private final LocalDateTime timeStamp;

    public BalanceResult(BalanceResultType error, String errorMessage,
                         LocalDateTime
            timeStamp) {
        this.errorType = error;
        this.errorMessage = errorMessage;
        balance = BALANCE_ERROR;
        this.timeStamp = timeStamp;
    }

    public BalanceResult(long balance, LocalDateTime timeStamp) {
        if (balance < 0) {
            this.errorType = BalanceResultType.NEGATIVE_BALANCE;
            this.errorMessage = "Negative balance reported";
        } else {
            this.errorType = BalanceResultType.OK;
            this.errorMessage = null;
        }
        this.balance = balance;
        this.timeStamp = timeStamp;
    }

    @JsonProperty
    public BalanceResultType getErrorType() {
        return errorType;
    }

    @JsonProperty
    public Optional<Integer> getErrorCode() {
        return errorType == BalanceResultType.OK ?
                Optional.empty() :
                Optional.of(errorType.value);
    }

    @JsonProperty
    public Optional<String> getError() {
        return errorType == BalanceResultType.OK ?
                Optional.empty() :
                Optional.ofNullable(errorMessage);
    }

    @JsonProperty
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    @JsonProperty
    public long getBalance() {
        return balance;
    }
}
