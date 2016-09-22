package com.quiptiq.wurmrest.resources;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Balance;
import com.quiptiq.wurmrest.api.Players;
import com.quiptiq.wurmrest.api.Transaction;
import com.quiptiq.wurmrest.rmi.PlayerService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Tests queries on the PlayerResource
 */
public class PlayerResourceTest {
    private static final PlayerService service = mock(PlayerService.class);

    private static final ResourceTestHelper helper =
            new ResourceTestHelper(new PlayerResource(service));

    @ClassRule
    public static final ResourceTestRule resources = helper.getResources();

    @Before
    public void setUp() {
        reset(service);
    }

    /**
     * Resource should return the correct player count when provided with filter=total
     */
    @Test
    public void correctPlayerCount() {
        int count = 67;
        Players expectedPlayers = new Players(count);
        when(service.getPlayerCount()).thenReturn(Result.success(count));
        Players players = helper.callGet(PlayerResource.PATH, Players.class, "filter", "total");
        assertEquals(expectedPlayers.getCount(), players.getCount());
        assertNull(players.getPlayers());
    }

    /**
     * Should throw {@link WebApplicationException} if the result is in error.
     */
    @Test(expected = WebApplicationException.class)
    public void throwOnError() {
        when(service.getPlayerCount()).thenReturn(Result.error("bad"));
        helper.callGet(PlayerResource.PATH, Players.class, "filter", "total");
    }

    /**
     * Should pass on a {@link WebApplicationException} if an exception is encountered with the
     * underlying service.
     */
    @Test(expected = WebApplicationException.class)
    public void passOnException() {
        when(service.getPlayerCount())
                .thenThrow(new WebApplicationException(Response.Status.BAD_GATEWAY));
        helper.callGet(PlayerResource.PATH, Players.class, "filter", "total");
    }

    /**
     * Should throw {@link WebApplicationException} if no filter is specified.
     */
    @Test
    public void throwOnEmptyFilter() {
        when(service.getPlayerCount()).thenReturn(Result.success(45));
        WebApplicationException caught = null;
        try {
            helper.callGet(PlayerResource.PATH, Players.class);
        } catch (WebApplicationException e) {
            caught = e;
        }
        assertNotNull(caught);
        assertEquals(Response.Status.BAD_REQUEST, caught.getResponse().getStatusInfo());
    }

    @Test
    public void validBalance() {
        String playerName = "Zaphod";
        Balance balance = new Balance(10);
        when(service.getBalance(playerName)).thenReturn(Result.success(balance));
        assertEquals(
                helper.callGet("/players/" + playerName + "/money", Balance.class)
                        .getBalance(),
                balance.getBalance());
    }

    @Test
    public void addMoney() {
        String playerName = "Trillian";
        Transaction transaction = new Transaction(40, "Test transaction");
        String successMessage = "Transaction successful";
        when(service.addMoney(playerName, transaction.getAmount(), transaction.getDetails()))
                .thenReturn(Result.success(successMessage));
        assertEquals(
                helper.callPost(
                        "/players/" + playerName + "/money",
                        new GenericType<Result<String>>() {},
                        transaction
                ).getValue(),
                successMessage);
    }
}
