package com.quiptiq.wurmrest.bank;

import java.time.LocalDateTime;

/**
 * Represents the result of a call to check a bank balance.
 */
public class BalanceResult {
    public enum BalanceResultType {
        OK(0),
        BAD_PLAYER_ID(1),
        UNKNOWN(2);

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
    private final String playerName;
    private final LocalDateTime timeStamp;

    public BalanceResult(String playerName, BalanceResultType error, String errorMessage,
                         LocalDateTime
            timeStamp) {
        this.errorType = error;
        this.errorMessage = errorMessage;
        balance = BALANCE_ERROR;
        this.playerName = playerName;
        this.timeStamp = timeStamp;
    }

    public BalanceResult(String playerName, long balance, LocalDateTime timeStamp) {
        if (balance < 0) {
            this.errorType = BalanceResultType.UNKNOWN;
            this.errorMessage = "Negative balance reported";
        } else {
            this.errorType = BalanceResultType.OK;
            this.errorMessage = null;
        }
        this.playerName = playerName;
        this.balance = balance;
        this.timeStamp = timeStamp;
    }

    public BalanceResultType getErrorType() {
        return errorType;
    }

    public boolean isError() {
        return errorMessage != null;
    }

    public String getPlayerName() {
        return playerName;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public long getBalance() {
        return balance;
    }
}
