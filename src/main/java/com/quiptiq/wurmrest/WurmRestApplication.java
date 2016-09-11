package com.quiptiq.wurmrest;

import javax.ws.rs.WebApplicationException;
import java.net.MalformedURLException;

import com.quiptiq.wurmrest.health.GameServiceHealthCheck;
import com.quiptiq.wurmrest.resources.BankResource;
import com.quiptiq.wurmrest.rmi.RmiGameService;
import com.quiptiq.wurmrest.rmi.RmiProvider;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application for starting up WurmRest
 */
public class WurmRestApplication extends Application<WurmRestConfiguration> {
    private static final Logger logger = LoggerFactory.getLogger(WurmRestApplication.class);

    private static final int UNKNOWN_ERROR = 1;

    public static void main(String[] args) {
        try {
            new WurmRestApplication().run(args);
        } catch (Exception e) {
            logger.error("Unexpected exception while running WurmRest application", e);
            System.exit(UNKNOWN_ERROR);
        }
    }

    @Override
    public String getName() {
        return "wurmrest";
    }

    @Override
    public void initialize(Bootstrap<WurmRestConfiguration> bootstrap) {

    }

    @Override
    public void run(WurmRestConfiguration wurmRestConfiguration, Environment environment) throws
            Exception {
        RmiGameService gameService;

        try {
            RmiProvider rmiProvider = wurmRestConfiguration.getRmiProviderFactory().build();
            gameService = new RmiGameService(rmiProvider);
        } catch (MalformedURLException e) {
            // Can't do anything if the URL is bad
            throw new WebApplicationException("Couldn't create service", e);
        }
        final BankResource bank = new BankResource(gameService);
        final GameServiceHealthCheck healthCheck = new GameServiceHealthCheck(gameService);
        environment.healthChecks().register("Game Service", healthCheck);
        environment.jersey().register(bank);
    }
}
