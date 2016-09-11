package com.quiptiq.wurmrest.rmi;

import com.quiptiq.wurmrest.Result;

/**
 * Administrative services
 */
public class AdminService extends RmiGameService {
    /**
     * Constructs the service using rmiProvider to provide a WebInterface. The WebInterface
     * is retrieved immediately so that a malformed URL may be discovered on the start of the
     * service.
     *
     * @param rmiProvider Provides the WebInterface for RMI invocation
     */
    public AdminService(RmiProvider rmiProvider) {
        super(rmiProvider);
    }

    /**
     * Broadcast the given message as a global announcement on the server.
     * @param message Message to broadcast
     * @return Success result with true value if the broadcast was completed, otherwise an error
     * Result.
     */
    public Result<Boolean> broadcast(String message) {
        return invoke("broadcast", webInterface -> {
            webInterface.broadcastMessage(getPassword(), message);
            return Result.success(true);
        });
    }

    /**
     * Initiates a shutdown with a timer and reason.
     * @param instigator Initiator of the shutdown
     * @param secondsTillShutdown Number of seconds till shutdown commences.
     * @param reason Reason for shutting the server down, broadcast to all players.
     * @return Success Result with true value if the broadcast was completed, otherwise an error
     * Result.
     */
    public Result<Boolean> startShutdown(String instigator, int secondsTillShutdown, String
            reason) {
        return invoke("startShutdown", webInterface -> {
            webInterface.startShutdown(getPassword(), instigator, secondsTillShutdown, reason);
            return Result.success(true);
        });
    }
}
