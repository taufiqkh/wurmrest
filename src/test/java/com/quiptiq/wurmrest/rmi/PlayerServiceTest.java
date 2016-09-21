package com.quiptiq.wurmrest.rmi;

import com.wurmonline.server.webinterface.WebInterface;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Tests for the Player RMI service
 */
public class PlayerServiceTest {
    private WebInterface webInterface;

    private final String password = "password";

    private PlayerService playerService;

    @Before
    public void setUp() throws Exception {
        RmiServiceTestHelper helper = new RmiServiceTestHelper(password);
        webInterface = helper.getWebInterface();
        playerService = new PlayerService(helper.getRmiProvider());
    }

    /**
     * The player service should report the correct player count.
     */
    @Test
    public void correctPlayerCount() throws Exception {
        int expectedCount = 42;
        when(webInterface.getPlayerCount(password)).thenReturn(expectedCount);
        assertEquals(expectedCount, playerService.getPlayerCount().getValue().intValue());
    }
}
