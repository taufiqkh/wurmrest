package com.quiptiq.wurmrest;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import com.quiptiq.wurmrest.bank.BalanceResult;
import com.wurmonline.server.webinterface.WebInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides access to the wurm service, encapsulating the calls to the game servers.
 */
public class RmiGameService implements GameService {
    private static final Logger logger = LoggerFactory.getLogger(RmiGameService.class);

    private WebInterface webInterface;

    private final String hostName;
    private final int port;
    private final String name;

    private WebInterface attemptLookup() throws MalformedURLException {
        String lookup = "//" + hostName + ":" + port + "/" + name;
        logger.info("Creating new web service at {}", lookup);
        WebInterface newInterface = null;
        try {
            newInterface = (WebInterface) java.rmi.Naming.lookup(lookup);
        } catch (NotBoundException | RemoteException e) {
            logger.error("Couldn't look up remote interface at " + lookup, e);
        }
        logger.info("Lookup performed successfully");
        return newInterface;
    }

    public RmiGameService(String hostName, int port, String name) throws MalformedURLException {
        this.hostName = hostName;
        this.port = port;
        this.name = name;
        webInterface = attemptLookup();
    }

    private WebInterface refreshWebInterface() {
        if (webInterface == null) {
            try {
                webInterface = attemptLookup();
            } catch (MalformedURLException e) {
                logger.error("URL Exception was not caught on initialisation", e);
                webInterface = null;
            }
        }
        return webInterface;
    }

    @FunctionalInterface
    private interface Invocation<T, R> {
        R apply(T t) throws RemoteException;
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
         * @return Result of the call, or null if the call failed.
         */
        <T, R> R invoke(String methodName, Invocation<T, R> invocation, T t) {
            if (webInterface == null) {
                if (refreshWebInterface() == null) {
                    logger.error("Unable to refresh while calling method {}", methodName);
                    return null;
                }
            }
            try {
                return invocation.apply(t);
            } catch (RemoteException e) {
                logger.error("Could not invoke method {}", methodName);
                logger.error("Exception encountered during invocation", e);
                try {
                    webInterface = refreshWebInterface();
                    return invocation.apply(t);
                } catch (RemoteException e2) {
                    logger.error("Could not invoke method again after refreshing web interface", e);
                }
            }
            return null;
        }
    }
    private final Invoker webInterfaceInvoker = new Invoker();

    private Invocation<String, Result<BalanceResult>> balanceInvocation = playerName -> {
        LocalDateTime timeStamp = LocalDateTime.now();
        long playerId = refreshWebInterface().getPlayerId(playerName);
        if (playerId > 0) {
            long balance = refreshWebInterface().getMoney(playerId, playerName);
            return Result.success(new BalanceResult(balance, timeStamp));
        } else {
            return Result.error("Bad player id");
        }
    };

    @Override
    public Result<BalanceResult> getBalance(String playerName) {
        Result<BalanceResult> result = webInterfaceInvoker.invoke("getBalance", balanceInvocation,
                playerName);
        Result<BalanceResult> foo = Result.error("asd");
        if (result == null) {
            result = Result.error("Could not invoke web interface");
        }
        return result;
    }
}
