package com.quiptiq.wurmrest;

import com.codahale.metrics.health.HealthCheck;
import com.quiptiq.wurmrest.rmi.RmiGameService;

/**
 * Health check for the WurmRest application. This runs minor tests to verify that the application
 * is running correctly.
 */
public class WurmRestHealthCheck extends HealthCheck {
    private final RmiGameService gameService;

    public WurmRestHealthCheck(RmiGameService gameService) {
        this.gameService = gameService;
    }

    @Override
    protected Result check() throws Exception {
        com.quiptiq.wurmrest.Result<Boolean> isRunning = gameService.isRunning();
        if (isRunning.isError()) {
            return Result.unhealthy("Error resulted when calling the Wurm service");
        } else if (!isRunning.getValue()) {
            return Result.unhealthy("Wurm service is not currently running");
        }
        return Result.healthy("Wurm service is currently running");
    }
}
