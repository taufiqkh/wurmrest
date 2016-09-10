package com.quiptiq.wurmrest.rmi;

import java.util.Optional;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.bank.Balance;
import com.wurmonline.server.webinterface.WebInterface;

/**
 * Provides access to the wurm service, encapsulating the calls to the game servers.
 */
public class RmiGameService {
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

    /**
     * Verifies that the stubbed interface has been created, attempts to create it if it has
     * not. Returns true if the interface already exists or was created successfully,
     * otherwise returns false.
     */
    boolean verifyServiceAvailable() {
        if (webInterface == null) {
            Optional<WebInterface> latest = rmiProvider.getOrRefreshWebInterface();
            if (!latest.isPresent()) {
                return false;
            }
            webInterface = latest.get();
        }
        return true;
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
        return webInterfaceInvoker.invoke(this, "getBalance", balanceInvocation, playerName);
    }
    /**
     * Invocation for {@link #getBalance(String)}
     */
    private final Invoker.Invocation<String, Balance> balanceInvocation = playerName -> {
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
