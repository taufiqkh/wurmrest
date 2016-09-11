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
    private final RmiProvider rmiProvider;

    private final String password;

    /**
     * Calls the web interface, wrapping each call in checks for null and attempts to get a
     * "refreshed" version from the provider if a call fails.
     */
    private final Invoker webInterfaceInvoker = new Invoker();

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

    private Result<Balance> addMoney(String playerName, long amount, String transactionDetails) {
        return invoke("addMoney", (WebInterface webInterface) -> {
            Map<String, String> moneyDetails = webInterface.addMoneyToBank(
                    password,
                    playerName,
                    amount,
                    transactionDetails);
            return null;
        });
    }
}
