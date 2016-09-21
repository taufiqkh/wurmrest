package com.quiptiq.wurmrest.rmi;

import java.util.Optional;

import com.wurmonline.server.webinterface.WebInterface;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Provides a mock WebInterface and RmiProvider for testing RMI services
 */
public class RmiServiceTestHelper {
    private final WebInterface webInterface;
    private final RmiProvider rmiProvider;

    public RmiServiceTestHelper(String password) {
        this.rmiProvider = mock(RmiProvider.class);
        when(rmiProvider.getPassword()).thenReturn(password);
        this.webInterface = mock(WebInterface.class);
        when(rmiProvider.getOrRefreshWebInterface()).thenReturn(Optional.of(webInterface));
    }

    public WebInterface getWebInterface() {
        return webInterface;
    }

    public RmiProvider getRmiProvider() {
        return rmiProvider;
    }
}
