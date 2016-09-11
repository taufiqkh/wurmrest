package com.quiptiq.wurmrest.rmi;

import java.util.Optional;

import com.quiptiq.wurmrest.Result;
import com.wurmonline.server.webinterface.WebInterface;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Tests for the admin service
 */
public class AdminServiceTest {
    private WebInterface webInterface;

    private String password = "password";

    private AdminService service;

    @Before
    public void setUp() {
        webInterface = mock(WebInterface.class);
        Optional<WebInterface> stubInterface = Optional.of(webInterface);
        RmiProvider provider = mock(RmiProvider.class);
        when(provider.getPassword()).thenReturn(password);
        when(provider.getOrRefreshWebInterface()).thenReturn(stubInterface);
        service = new AdminService(provider);
    }

    @Test
    public void successfulAnnouncement() throws Exception {
        String announcement = "Fee fi fo fum";
        Result<Boolean> result = service.broadcast(announcement);
        verify(webInterface).broadcastMessage(password, announcement);
        assertTrue(result.isSuccess());
        assertTrue(result.getValue());
    }

    @Test
    public void startShutdown() throws Exception {
        String instigator = "test";
        int timer = 600;
        String reason = "because";
        Result<Boolean> result = service.startShutdown(instigator, timer, reason);
        assertTrue(result.isSuccess());
        assertTrue(result.getValue());
    }
}
