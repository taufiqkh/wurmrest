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
import com.quiptiq.wurmrest.rmi.BankService;
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
    private static final BankService testService = mock(BankService.class);

    private static final ResourceTestHelper helper =
            new ResourceTestHelper(new BankResource(testService));

    @ClassRule
    public static final ResourceTestRule resources = helper.getResources();

    @After
    public void tearDown() {
        reset(testService);
    }

    @Test
    public void validBalance() {
        String playerName = "Zaphod";
        Balance balance = new Balance(10);
        when(testService.getBalance(playerName)).thenReturn(Result.success(balance));
        assertEquals(
                helper.callGet("/bank/" + playerName + "/money", Balance.class).getBalance(),
                balance.getBalance());
    }

    @Test
    public void addMoney() {
        String playerName = "Trillian";
        Transaction transaction = new Transaction(40, "Test transaction");
        String successMessage = "Transaction successful";
        when(testService.addMoney(playerName, transaction.getAmount(), transaction.getDetails()))
                .thenReturn(Result.success(successMessage));
        assertEquals(helper.callPost("/bank/" + playerName + "/money", RawResult.class, transaction)
                        .getValue(),
                successMessage);
    }
}
