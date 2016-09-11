package com.quiptiq.wurmrest.health;

import com.codahale.metrics.health.HealthCheck;
import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.rmi.RmiGameService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the game service health check
 */
public class GameServiceHealthCheckTest {
    RmiGameService mockService;

    @Before
    public void setUp() {
        mockService = mock(RmiGameService.class);
    }

    @Test
    public void unhealthyIfServiceErrors() throws Exception {
        when(mockService.isRunning()).thenReturn(Result.error("Bad stuff"));
        GameServiceHealthCheck healthCheck = new GameServiceHealthCheck(mockService);
        HealthCheck.Result result = healthCheck.check();
        assertFalse(result.isHealthy());
    }

    @Test
    public void unhealthyIfServiceIsNotRunning() throws Exception {
        when(mockService.isRunning()).thenReturn(Result.success(false));
        GameServiceHealthCheck healthCheck = new GameServiceHealthCheck(mockService);
        HealthCheck.Result result = healthCheck.check();
        assertFalse(result.isHealthy());
    }

    @Test
    public void healthyIfServiceIsRunning() throws Exception {
        when(mockService.isRunning()).thenReturn(Result.success(true));
        GameServiceHealthCheck healthCheck = new GameServiceHealthCheck(mockService);
        HealthCheck.Result result = healthCheck.check();
        assertTrue(result.isHealthy());
    }
}
