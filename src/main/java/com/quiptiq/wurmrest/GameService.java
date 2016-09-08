package com.quiptiq.wurmrest;

import com.quiptiq.wurmrest.bank.BalanceResult;

/**
 * Interface for wurm service provision.
 */
interface GameService {
    /**
     * Get the balance for the given player.
     * @param playerName Name of the player for which the balance is returned.
     * @return Balance for player with the given name.
     */
    BalanceResult getBalance(String playerName);
}
