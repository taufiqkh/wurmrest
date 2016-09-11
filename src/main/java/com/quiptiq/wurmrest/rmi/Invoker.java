package com.quiptiq.wurmrest.rmi;

import java.rmi.RemoteException;
import java.util.Optional;

import com.quiptiq.wurmrest.Result;
import com.wurmonline.server.webinterface.WebInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calls invocations, wrapping the call in web interface refreshes. This means that the service
 * can continue even if the interface is down for a period, as subsequent calls will update the
 * interface once it comes up again. The possibility exists for a remote interface to fail between
 * calls, however; invocations should be written with this in mind.
 */
public class Invoker {
    private static final Logger logger = LoggerFactory.getLogger(Invoker.class);

    /**
     * The web interface stub cannot be instantiated.
     */
    private static final String UNABLE_TO_CREATE_STUB = "Could not create stub for web interface";

    /**
     * Calling the web interface results in an error, even after a refresh.
     */
    private static final String UNABLE_TO_CALL_REMOTE = "Unable to call remote interface";

    /**
     * Error occurred attempting to invoke after a refresh of the service.
     */
    private static final String ERROR_INVOKING_AFTER_REFRESH =
            "Could not invoke method again after refreshing web interface";

    /**
     * Invocation, called to execute a method on the service.
     * @param <R> Type to be wrapped in the Result.
     */
    @FunctionalInterface
    interface Invocation<R> {
        Result<R> apply(WebInterface webInterface) throws RemoteException;
    }

    /**
     * Invokes the method using the specified invocation function, with Result wrapping the return
     * type. The call is wrapped in try/catch in case of an invalid service. If an error occurs
     * when the call is first made, the interface is refreshed and called again. If the call
     * fails again the method returns an error Result.
     *
     * @param methodName Name of the method, for logging
     * @param invocation Invocation
     * @param <R>        Return type to wrap in a Result
     * @return Successful Result if the call completed successfully, otherwise an error Result.
     */
    public <R> Result<R> invoke(RmiProvider rmiProvider, String methodName,
                                Invocation<R> invocation) {
        Optional<WebInterface> webInterface = rmiProvider.getOrRefreshWebInterface();
        if (webInterface.isPresent()) {
            try {
                return invocation.apply(webInterface.get());
            } catch (RemoteException e) {
                logger.error("Could not invoke method {}", methodName);
                logger.error("Exception encountered during invocation", e);
                try {
                    // try again
                    webInterface = rmiProvider.getOrRefreshWebInterface();
                    if (webInterface.isPresent()) {
                        return invocation.apply(webInterface.get());
                    }
                } catch (RemoteException e2) {
                    logger.error(ERROR_INVOKING_AFTER_REFRESH, e);
                }
                return Result.error(UNABLE_TO_CALL_REMOTE);
            }
        } else {
            return Result.error(UNABLE_TO_CREATE_STUB);
        }
    }
}