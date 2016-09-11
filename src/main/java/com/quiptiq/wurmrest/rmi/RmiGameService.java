package com.quiptiq.wurmrest.rmi;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;
import java.util.Optional;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.bank.Balance;
import com.wurmonline.server.webinterface.WebInterface;

/**
 * Provides access to the wurm service, encapsulating the calls to the game servers.
 */
@ThreadSafe
public class RmiGameService {
    /**
     * Calls the web interface, wrapping each call in checks for null and attempts to get a
     * "refreshed" version from the provider if a call fails.
     */
    private final Invoker webInterfaceInvoker = new Invoker();

    /**
     * For a map returned by a web interface, the existence of this key should indicate that an
     * error occurred and the value at this key contains the message.
     */
    private static final String ERROR_KEY = "error";

    /**
     * For a map returned by a web interface, the existence of this key should indicate that the
     * method executed successfully and the value at this key contains the message.
     */
    private static final String SUCCESS_KEY = "ok";

    /**
     * Provides thread-safe access to the web interface stub
     */
    private final RmiProvider rmiProvider;

    /**
     * Password to the web interface.
     */
    private final String password;

    /**
     * None of the expected values from a web interface call were returned.
     */
    private static final String ERROR_NO_EXPECTED_VALUES = "No expected values were returned";

    /**
     * Constructs the RmiGameService using rmiProvider to provide a WebInterface. The WebInterface
     * is retrieved immediately so that a malformed URL may be discovered on the start of the
     * service.
     *
     * @param rmiProvider Provides the WebInterface for RMI invocation
     */
    public RmiGameService(RmiProvider rmiProvider) {
        this.rmiProvider = rmiProvider;
        this.password = rmiProvider.getPassword();
        rmiProvider.getOrRefreshWebInterface();
    }

    /**
     * Convenience method for invoking a method on the web interface.
     * @param methodName Name of the method, for logging
     * @param invocation Invocation to execute
     * @param <R> Result return type
     * @return Result wrapping the given return type.
     */
    private <R> Result<R> invoke(String methodName, Invoker.Invocation<R> invocation) {
        return webInterfaceInvoker.invoke(rmiProvider, methodName, invocation);
    }

    /**
     * Get the balance for the given player.
     * @param playerName Name of the player for which the balance is returned.
     * @return Balance for player with the given name.
     */
    public Result<Balance> getBalance(String playerName) {
        return invoke("getBalance", (WebInterface webInterface) -> {
            long playerId = webInterface.getPlayerId(password, playerName);
            if (playerId > 0) {
                long balance = webInterface.getMoney(password, playerId, playerName);
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
                    password,
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
