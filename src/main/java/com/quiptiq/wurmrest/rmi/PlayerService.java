package com.quiptiq.wurmrest.rmi;

import com.quiptiq.wurmrest.Result;

/**
 * Provides services for player details
 */
public class PlayerService extends RmiGameService {
    /**
     * Constructs the RmiGameService using rmiProvider to provide a WebInterface. The WebInterface
     * is retrieved immediately so that a malformed URL may be discovered on the start of the
     * service.
     *
     * @param rmiProvider Provides the WebInterface for RMI invocation
     */
    public PlayerService(RmiProvider rmiProvider) {
        super(rmiProvider);
    }

    /**
     * @return Count of players on the server cluster
     */
    public Result<Integer> getPlayerCount() {
        return invoke("getPlayerCount",
                webInterface -> Result.success(webInterface.getPlayerCount(getPassword())));
    }
}
