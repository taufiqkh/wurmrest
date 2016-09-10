package com.quiptiq.wurmrest.rmi;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Optional;

import com.wurmonline.server.webinterface.WebInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides RMI calls to the WebInterface, with refresh functionality to allow the recreation of
 * the stub.
 */
public class RmiProvider {
    private static final Logger logger = LoggerFactory.getLogger(RmiProvider.class);

    /**
     * URL for the lookup
     */
    private final String lookup;

    /**
     * RMI stub to WebInterface
     */
    private WebInterface webInterface;

    /**
     * Performs the lookup for the WebInterface.
     * @return WebInterface stub, or null if the lookup failed.
     * @throws MalformedURLException if the URL cannot be formed.
     */
    private WebInterface attemptLookup() throws MalformedURLException {
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

    /**
     * Constructs a provider that will provide instances of the RMI WebInterface stub.
     * @param hostName Name of the host on which the RMI service resides
     * @param port Port on which the RMI service listens
     * @param objectName Name of the object used by the WebInterface service
     * @throws MalformedURLException if the hostName, port and object name cannot be combined to
     * form a valid URL.
     */
    public RmiProvider(String hostName, int port, String objectName) throws MalformedURLException {
        this.lookup = "//" + hostName + ":" + port + "/" + objectName;
        webInterface = attemptLookup();
    }

    /**
     * Provides the current RMI stub to the Wurm service. If it is null, attempts a lookup and
     * returns the result wrapped in Optional. If the lookup is unsuccessful, returns an empty
     * Optional.
     * @return Optional that contains the WebInterface if not null or if looked up successfully,
     * otherwise an empty Optional.
     */
    public Optional<WebInterface> getOrRefreshWebInterface() {
        if (webInterface == null) {
            try {
                webInterface = attemptLookup();
            } catch (MalformedURLException e) {
                logger.error("URL Exception was not caught on initialisation", e);
                return null;
            }
        }
        return Optional.ofNullable(webInterface);
    }

}
