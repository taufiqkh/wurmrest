package com.quiptiq.wurmrest.rmi;

import javax.ws.rs.WebApplicationException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Balance;
import com.wurmonline.server.webinterface.WebInterface;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for the Player RMI service
 */
public class PlayerServiceTest {
    private WebInterface webInterface;

    private final String password = "password";

    private PlayerService service;

    @Before
    public void setUp() throws Exception {
        RmiServiceTestHelper helper = new RmiServiceTestHelper(password);
        webInterface = helper.getWebInterface();
        service = new PlayerService(helper.getRmiProvider());
    }

    /**
     * The player service should report the correct player count.
     */
    @Test
    public void correctPlayerCount() throws Exception {
        int expectedCount = 42;
        when(webInterface.getPlayerCount(password)).thenReturn(expectedCount);
        assertEquals(expectedCount, service.getPlayerCount().getValue().intValue());
    }

    /**
     * Failed lookup should not cause the service constructor to fail, as this error is recoverable
     * if subsequent lookups are successful.
     */
    @Test
    public void failedLookupShouldNotFailService() throws Exception {
        RmiProvider failProvider = mock(RmiProvider.class);
        when(failProvider.getOrRefreshWebInterface()).thenReturn(Optional.empty());
        new RmiGameService(failProvider);
    }

    @Test
    public void shouldErrorForUnknownPlayer() throws Exception {
        String playerName = "Fred";
        when(webInterface.getPlayerId(password, playerName)).thenReturn(-1L);
        assertTrue("Unknown player should result in error",
                service.getBalance(playerName).isError());
    }

    /**
     * If the web interface errors and continues to error, should return error result.
     */
    @Test(expected = WebApplicationException.class)
    public void shouldErrorIfWebInterfaceAlwaysErrors() throws Exception {
        String playerName = "Wilma";
        when(webInterface.getPlayerId(password, playerName)).thenThrow(RemoteException.class);
        service.getBalance(playerName);
    }


    @Test
    public void shouldRecoverIfWebInterfaceRecovers() throws Exception {
        String playerName = "Barney";
        when(webInterface.getPlayerId(password, playerName))
                .thenThrow(RemoteException.class)
                .thenReturn(-1L);
        assertTrue("Unknown player should still result in error",
                service.getBalance(playerName).isError());

        long playerId = 1;
        long balance = 42;
        when(webInterface.getPlayerId(password, playerName))
                .thenThrow(RemoteException.class)
                .thenReturn(playerId);
        when(webInterface.getMoney(password, playerId, playerName)).thenReturn(balance);
        Result<Balance> result = service.getBalance(playerName);
        assertTrue("Known player should still result in success",
                result.isSuccess());
        assertEquals("Correct balance should still be returned after error recovery",
                balance,
                result.getValue().getBalance());
    }

    @Test
    public void shouldReturnCorrectBalance() throws Exception {
        String playerName = "Betty";
        long playerId = 1;
        long balance = 42;
        when(webInterface.getPlayerId(password, playerName)).thenReturn(playerId);
        when(webInterface.getMoney(password, playerId, playerName)).thenReturn(balance);
        Result<Balance> result = service.getBalance(playerName);
        assertTrue("Known player with valid balance should result in success", result.isSuccess());
        assertEquals("Should return correct balance", balance, result.getValue().getBalance());
    }

    @Test
    public void shouldAddMoney() throws Exception {
        String playerName = "Bambam";
        long amount = 40;
        String transactionDetails = "Test transaction";
        Map<String, String> wiResult = new HashMap<>();
        String message = "Success message";
        wiResult.put("ok", message);
        when(webInterface.addMoneyToBank(password, playerName, amount, transactionDetails))
                .thenReturn(wiResult);
        Result<String> result = service.addMoney(playerName, amount, transactionDetails);
        assertTrue("Adding money should be successful", result.isSuccess());
        assertEquals("Should return correct message", message, result.getValue());
    }
}
