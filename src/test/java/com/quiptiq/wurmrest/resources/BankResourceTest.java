package com.quiptiq.wurmrest.resources;

import javax.ws.rs.core.GenericType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.ResultDeserializer;
import com.quiptiq.wurmrest.api.Balance;
import com.quiptiq.wurmrest.api.Transaction;
import com.quiptiq.wurmrest.rmi.BankService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
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

    @Before
    public void setUp() {
        SimpleModule module = new SimpleModule().addDeserializer(Result.class, new ResultDeserializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
    }

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
                helper.callGet("/bank/players/" + playerName + "/money", Balance.class)
                        .getBalance(),
                balance.getBalance());
    }

    @Test
    public void addMoney() {
        String playerName = "Trillian";
        Transaction transaction = new Transaction(40, "Test transaction");
        String successMessage = "Transaction successful";
        when(testService.addMoney(playerName, transaction.getAmount(), transaction.getDetails()))
                .thenReturn(Result.success(successMessage));
        assertEquals(
                helper.callPost(
                        "/bank/players/" + playerName + "/money",
                        new GenericType<Result<String>>() {},
                        transaction
                ).getValue(),
                successMessage);
    }
}
