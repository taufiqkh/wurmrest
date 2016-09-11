package com.quiptiq.wurmrest.rmi;

import java.util.Map;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Balance;
import com.wurmonline.server.webinterface.WebInterface;

/**
 * Provides access to bank services via RMI
 */
public class BankService extends RmiGameService {
    public BankService(RmiProvider provider) {
        super(provider);
    }

    /**
     * Get the balance for the given player.
     * @param playerName Name of the player for which the balance is returned.
     * @return Balance for player with the given name.
     */
    public Result<Balance> getBalance(String playerName) {
        return invoke("getBalance", (WebInterface webInterface) -> {
            long playerId = webInterface.getPlayerId(getPassword(), playerName);
            if (playerId > 0) {
                long balance = webInterface.getMoney(getPassword(), playerId, playerName);
                return Result.success(new Balance(balance));
            } else {
                return Result.error("Bad player id");
            }
        });
    }

    /**
     * Add money to the given player's bank account.
     * @param playerName Name of the player
     * @param amount Amount of money to add
     * @param transactionDetails Details of the transaction, for logging
     * @return Result with a balance amount
     */
    public Result<String> addMoney(String playerName, long amount, String transactionDetails) {
        return invoke("addMoney", (WebInterface webInterface) -> {
            Map<String, String> moneyDetails = webInterface.addMoneyToBank(
                    getPassword(),
                    playerName,
                    amount,
                    transactionDetails);
            if (moneyDetails.containsKey(ERROR_KEY)) {
                return Result.error(moneyDetails.get(ERROR_KEY));
            } else if (moneyDetails.containsKey(SUCCESS_KEY)) {
                return Result.success(moneyDetails.get(SUCCESS_KEY));
            }
            return Result.error(ERROR_NO_EXPECTED_VALUES);
        });
    }
}
