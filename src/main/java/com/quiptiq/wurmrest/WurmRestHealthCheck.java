package com.quiptiq.wurmrest;

import com.codahale.metrics.health.HealthCheck;

/**
 * Health check for the WurmRest application. This runs minor tests to verify that the application
 * is running correctly.
 */
public class WurmRestHealthCheck extends HealthCheck {
    private final String template;

    public WurmRestHealthCheck(String template) {
        this.template = template;
    }

    @Override
    protected Result check() throws Exception {
        final String saying = String.format(template, "TEST");
        if (!saying.contains("TEST")) {
            return Result.unhealthy("Template doesn't include a name");
        }
        return Result.healthy();
    }
}
