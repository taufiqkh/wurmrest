package com.quiptiq.wurmrest.rmi;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
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
 *
 * <p>
 * <b>Implementation Note:</b>
 * <p>
 * While this class is thread safe, it only use a simple write lock for the volatile webInterface.
 * This does not prevent the pathological case where several threads find a null or invalid
 * webInterface and all attempt to refresh it at once, causing several lookups all in succession.
 * For the expected use case this is not expected to be a major concern.
 */
@ThreadSafe
public class RmiProvider {
    private static final Logger logger = LoggerFactory.getLogger(RmiProvider.class);

    /**
     * URL for the lookup
     */
    private final String lookup;

    /**
     * Password used in interface calls
     */
    private final String password;

    /**
     * RMI stub to WebInterface
     */
    @GuardedBy("webInterfaceWrite")
    private volatile WebInterface webInterface;

    private final Object webInterfaceWrite = new Object();

    /**
     * Performs the lookup for the WebInterface.
     * @return WebInterface stub, or null if the lookup failed.
     * @throws MalformedURLException if the URL cannot be formed.
     */
    private WebInterface attemptLookup() throws MalformedURLException {
        logger.info("Creating new web service from RMI lookup at {}", lookup);
        WebInterface newInterface = null;
        try {
            newInterface = (WebInterface) java.rmi.Naming.lookup(lookup);
            logger.info("Lookup performed successfully");
        } catch (NotBoundException | RemoteException e) {
            logger.error("Couldn't look up remote interface at " + lookup, e);
        }
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
    public RmiProvider(String hostName, int port, String objectName, String password) throws
            MalformedURLException {
        this.lookup = "//" + hostName + ":" + port + "/" + objectName;
        this.password = password;
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
        WebInterface latestWebInterface = webInterface;
        if (latestWebInterface == null) {
            try {
                latestWebInterface = attemptLookup();
                synchronized (webInterfaceWrite) {
                    webInterface = latestWebInterface ;
                }
            } catch (MalformedURLException e) {
                logger.error("URL Exception was not caught on initialisation", e);
            }
        }
        return Optional.ofNullable(latestWebInterface );
    }

    /**
     * Each call specifies a common password. This password is returned for use in calls.
     * @return password for use in calls
     */
    public String getPassword() {
        return password;
    }
}
