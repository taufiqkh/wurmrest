package com.quiptiq.wurmrest.resources;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import com.quiptiq.wurmrest.RawResult;
import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Balance;
import com.quiptiq.wurmrest.api.Transaction;
import com.quiptiq.wurmrest.rmi.RmiGameService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests for the /bank resource
 */
public class BankResourceTest {
    private static final RmiGameService testService = mock(RmiGameService.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new BankResource(testService))
            .build();

    @After
    public void tearDown() {
        reset(testService);
    }

    private <R> R callGet(String path, Class<R> returnClass) {
        return resources.client().target(path).request().get(returnClass);
    }

    private <R, T> R callPost(String path, Class<R> returnClass, T t) {
        WebTarget target = resources.client().target(path);
        Response response = target.request().post(Entity.json(t));
        return response.readEntity(returnClass);
    }

    @Test
    public void validBalance() {
        String playerName = "Zaphod";
        Balance balance = new Balance(10);
        when(testService.getBalance(playerName)).thenReturn(Result.success(balance));
        assertEquals(
                callGet("/bank/" + playerName + "/money", Balance.class).getBalance(),
                balance.getBalance());
    }

    @Test
    public void addMoney() {
        String playerName = "Trillian";
        Transaction transaction = new Transaction(40, "Test transaction");
        String successMessage = "Transaction successful";
        when(testService.addMoney(playerName, transaction.getAmount(), transaction.getDetails()))
                .thenReturn(Result.success(successMessage));
        assertEquals(callPost("/bank/" + playerName + "/money", RawResult.class, transaction)
                        .getValue(),
                successMessage);
    }
}
