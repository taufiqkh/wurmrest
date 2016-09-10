package com.quiptiq.wurmrest.rmi;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Optional;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.bank.BalanceResult;
import com.wurmonline.server.webinterface.WebInterface;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the RMI game service calls by providing a stub webinterface.
 */
public class RmiGameServiceTest {
    private RmiProvider provider;
    private WebInterface webInterface;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<WebInterface> stubInterface;

    RmiGameService service;

    @Before
    public void setup() throws Exception {
        webInterface = mock(WebInterface.class);
        stubInterface = Optional.of(webInterface);
        provider = mock(RmiProvider.class);
        when(provider.getOrRefreshWebInterface()).thenReturn(stubInterface);
        service = new RmiGameService(provider);
    }

    /**
     * When a provider has a bad URL the RmiGameService should fail fast, throwing
     * MalformedURLException as soon as it is instantiated.
     */
    @Test(expected = MalformedURLException.class)
    public void badUrlShouldFailFast() throws MalformedURLException {
        RmiProvider failProvider = mock(RmiProvider.class);
        when(failProvider.getOrRefreshWebInterface()).thenThrow(MalformedURLException.class);
        new RmiGameService(failProvider);
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
        when(webInterface.getPlayerId(playerName)).thenReturn(-1L);
        assertTrue("Unknown player should result in error",
                service.getBalance(playerName).isError());
    }

    /**
     * If the web interface errors and continues to error, should return error result.
     */
    @Test
    public void shouldErrorIfWebInterfaceAlwaysErrors() throws Exception {
        String playerName = "Wilma";
        when(webInterface.getPlayerId(playerName)).thenThrow(RemoteException.class);
        Result<BalanceResult> result = service.getBalance(playerName);
        assertTrue("Interface errors should result in error", result.isError());
    }


    @Test
    public void shouldRecoverIfWebInterfaceRecovers() throws Exception {
        String playerName = "Barney";
        when(webInterface.getPlayerId(playerName))
                .thenThrow(RemoteException.class)
                .thenReturn(-1L);
        assertTrue("Unknown player should still result in error",
                service.getBalance(playerName).isError());

        long playerId = 1;
        long balance = 42;
        when(webInterface.getPlayerId(playerName))
                .thenThrow(RemoteException.class)
                .thenReturn(playerId);
        when(webInterface.getMoney(playerId, playerName)).thenReturn(balance);
        Result<BalanceResult> result = service.getBalance(playerName);
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
        when(webInterface.getPlayerId(playerName)).thenReturn(playerId);
        when(webInterface.getMoney(playerId, playerName)).thenReturn(balance);
        Result<BalanceResult> result = service.getBalance(playerName);
        assertTrue("Known player with valid balance should result in success", result.isSuccess());
        assertEquals("Should return correct balance", balance, result.getValue().getBalance());
    }

}
