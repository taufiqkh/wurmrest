package com.quiptiq.wurmrest.rmi;

import javax.annotation.Nonnull;
import java.rmi.RemoteException;
import java.util.Optional;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.bank.Balance;
import com.wurmonline.server.webinterface.WebInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides access to the wurm service, encapsulating the calls to the game servers.
 */
public class RmiGameService {
    private static final Logger logger = LoggerFactory.getLogger(RmiGameService.class);

    private WebInterface webInterface;

    private final RmiProvider rmiProvider;

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
        Optional<WebInterface> newWebInterface = rmiProvider.getOrRefreshWebInterface();
        if (newWebInterface.isPresent()) {
            webInterface = newWebInterface.get();
        }
    }

    @FunctionalInterface
    private interface Invocation<T, R> {
        Result<R> apply(T t) throws RemoteException;
    }

    /**
     * Calls invocations, wrapping the call in web interface refreshes.
     *
     */
    private class Invoker {
        /**
         * Invokes the method using the specified invocation function, with argument and return
         * types. The call is wrapped in try/catch in the case of a null or invalid web interface.
         * If an error occurs when the call is first made, the interface is refreshed and called
         * again. If the call fails again the method returns null.
         *
         * @param methodName Name of the method, for logging
         * @param invocation Invocation
         * @param t Argument to use
         * @param <T> Type of the argument
         * @param <R> Return type
         * @return Successful Result if the call completed successfully, otherwise an error Result.
         */
        @Nonnull
        <T, R> Result<R> invoke(String methodName, Invocation<T, R> invocation, T t) {
            if (webInterface == null) {
                Optional<WebInterface> latest = rmiProvider.getOrRefreshWebInterface();
                if (!latest.isPresent()) {
                    return Result.error("Could not create stub for web interface");
                }
                webInterface = latest.get();
            }
            try {
                return invocation.apply(t);
            } catch (RemoteException e) {
                logger.error("Could not invoke method {}", methodName);
                logger.error("Exception encountered during invocation", e);
                try {
                    // try again
                    Optional<WebInterface> latest = rmiProvider.getOrRefreshWebInterface();
                    if (latest.isPresent()) {
                        return invocation.apply(t);
                    }
                } catch (RemoteException e2) {
                    logger.error("Could not invoke method again after refreshing web interface", e);
                }
                return Result.error("Unable to call remote interface");
            }
        }
    }

    /**
     * Convenience method to retrieve the password, especially given that the rmiProvider referred
     * to by lambdas may not be initialized when the lambda is created.
     * @return Password for the RMI interface.
     */
    private String getPassword() {
        return rmiProvider.getPassword();
    }

    /**
     * Get the balance for the given player.
     * @param playerName Name of the player for which the balance is returned.
     * @return Balance for player with the given name.
     */
    public Result<Balance> getBalance(String playerName) {
        return webInterfaceInvoker.invoke("getBalance", balanceInvocation, playerName);
    }

    private final Invocation<String, Balance> balanceInvocation = playerName -> {
        String password = getPassword();
        long playerId = webInterface.getPlayerId(password, playerName);
        if (playerId > 0) {
            long balance = webInterface.getMoney(password, playerId, playerName);
            return Result.success(new Balance(balance));
        } else {
            return Result.error("Bad player id");
        }
    };
}
