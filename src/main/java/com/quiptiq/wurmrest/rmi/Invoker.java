package com.quiptiq.wurmrest.rmi;

import javax.annotation.Nonnull;
import java.rmi.RemoteException;

import com.quiptiq.wurmrest.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Calls invocations, wrapping the call in web interface refreshes.
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

    @FunctionalInterface
    interface NoArgInvocation<R> {
        Result<R> apply() throws RemoteException;
    }

    @FunctionalInterface
    interface Invocation<T, R> {
        Result<R> apply(T t) throws RemoteException;
    }

    @FunctionalInterface
    interface BiInvocation<T, U, R> {
        Result<R> apply(T t, U u) throws RemoteException;
    }

    public <R> Result<R> invoke(RmiGameService service, String methodName,
                                    NoArgInvocation<R> invocation) {
        if (!service.verifyServiceAvailable()) {
            return Result.error(UNABLE_TO_CREATE_STUB);
        }
        try {
            return invocation.apply();
        } catch (RemoteException e) {
            logger.error("Could not invoke method {}", methodName);
            logger.error("Exception encountered during invocation", e);
            try {
                // try again
                if (service.verifyServiceAvailable()) {
                    return invocation.apply();
                }
            } catch (RemoteException e2) {
                logger.error("Could not invoke method again after refreshing web interface", e);
            }
            return Result.error(UNABLE_TO_CALL_REMOTE);
        }
    }

    /**
     * Invokes the method using the specified invocation function, with argument and return
     * types. The call is wrapped in try/catch in the case of a null or invalid web interface.
     * If an error occurs when the call is first made, the interface is refreshed and called
     * again. If the call fails again the method returns null.
     *
     * @param methodName Name of the method, for logging
     * @param invocation Invocation
     * @param t          Argument to use
     * @param <T>        Type of the argument
     * @param <R>        Return type
     * @return Successful Result if the call completed successfully, otherwise an error Result.
     */
    @Nonnull
    <T, R> Result<R> invoke(RmiGameService service, String methodName,
                            Invocation<T, R> invocation, T t) {
        return invoke(service, methodName, () -> invocation.apply(t));
    }
}