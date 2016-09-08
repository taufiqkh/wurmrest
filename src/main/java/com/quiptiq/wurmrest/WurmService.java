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
public class WurmService {
    private static final Logger logger = LoggerFactory.getLogger(WurmService.class);

    private WebInterface webInterface;
    private static final int BALANCE_INVALID = -1;

    private static final String ERROR_MESSAGE_NULL = "Error message was returned but was null";

    /**
     * Key for maps that, when present, indicates an error.
     */
    private static final String ERROR_KEY = "Error";

    public WurmService(String hostname, int port, String name) throws RemoteException,
            NotBoundException, MalformedURLException {
        String lookup = "//" + hostname + ":" + port + "/" + name;
        logger.info("Creating new web service at {}", lookup);
        webInterface = (WebInterface) java.rmi.Naming.lookup(lookup);
        logger.info("Lookup performed successfully");
    }

    private Optional<String> getErrorMessage(Map<String, ?> map) {
        Object message = map.get(ERROR_KEY);
        if (message == null) {
            return Optional.empty();
        }
        String errorMessage;
        try {
            errorMessage = (String) message;
        } catch (ClassCastException e) {
            errorMessage = "Error returned but could not cast to String: " + message.toString();
        }
        return Optional.of(errorMessage);
    }

    public BalanceResult getBalance(String playerName) throws RemoteException {
        LocalDateTime timeStamp = LocalDateTime.now();
        long playerId = webInterface.getPlayerId(playerName);
        if (playerId > 0) {
            long balance = webInterface.getMoney(playerId, playerName);
            return new BalanceResult(playerName, balance, timeStamp);
        } else {
            return new BalanceResult(playerName, BalanceResult.BalanceResultType.BAD_PLAYER_ID,
                    "Bad player id", timeStamp);
        }
    }
}
