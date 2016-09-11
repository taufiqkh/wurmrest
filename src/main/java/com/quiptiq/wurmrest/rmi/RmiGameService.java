package com.quiptiq.wurmrest.rmi;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Map;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Balance;
import com.wurmonline.server.webinterface.WebInterface;

/**
 * Provides access to the wurm service, encapsulating the calls to the game servers.
 */
@ThreadSafe
public class RmiGameService {
    /**
     * Calls the web interface, wrapping each call in checks for null and attempts to get a
     * "refreshed" version from the provider if a call fails.
     */
    private final Invoker webInterfaceInvoker = new Invoker();

    /**
     * For a map returned by a web interface, the existence of this key should indicate that an
     * error occurred and the value at this key contains the message.
     */
    protected static final String ERROR_KEY = "error";

    /**
     * For a map returned by a web interface, the existence of this key should indicate that the
     * method executed successfully and the value at this key contains the message.
     */
    protected static final String SUCCESS_KEY = "ok";

    /**
     * Provides thread-safe access to the web interface stub
     */
    private final RmiProvider rmiProvider;

    /**
     * Password to the web interface.
     */
    private final String password;

    /**
     * None of the expected values from a web interface call were returned.
     */
    protected static final String ERROR_NO_EXPECTED_VALUES = "No expected values were returned";

    /**
     * Constructs the RmiGameService using rmiProvider to provide a WebInterface. The WebInterface
     * is retrieved immediately so that a malformed URL may be discovered on the start of the
     * service.
     *
     * @param rmiProvider Provides the WebInterface for RMI invocation
     */
    public RmiGameService(RmiProvider rmiProvider) {
        this.rmiProvider = rmiProvider;
        this.password = rmiProvider.getPassword();
        rmiProvider.getOrRefreshWebInterface();
    }

    /**
     * @return Password for the RMI service.
     */
    protected String getPassword() {
        return password;
    }

    /**
     * Convenience method for invoking a method on the web interface.
     * @param methodName Name of the method, for logging
     * @param invocation Invocation to execute
     * @param <R> Result return type
     * @return Result wrapping the given return type.
     */
    protected <R> Result<R> invoke(String methodName, Invoker.Invocation<R> invocation) {
        return webInterfaceInvoker.invoke(rmiProvider, methodName, invocation);
    }

    /**
     * Whether or not the service is currently running.
     * @return Success result with true if the game is running, false if it is not and an
     * error result if the service could not be contacted.
     */
    public Result<Boolean> isRunning() {
        return invoke("isRunning",
                (WebInterface webinterface) -> Result.success(webinterface.isRunning(password)));
    }
}
