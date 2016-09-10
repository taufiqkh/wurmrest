package com.quiptiq.wurmrest.resources;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.bank.Balance;
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

    @Test
    public void validBalance() {
        String playerName = "Zaphod";
        Balance balance = new Balance(10);
        when(testService.getBalance(playerName)).thenReturn(Result.success(balance));
        assertEquals(
                resources.client()
                        .target("/bank/" + playerName + "/balance")
                        .request()
                        .get(Balance.class)
                        .getBalance(),
                balance.getBalance());
    }

}
