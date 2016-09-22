package com.quiptiq.wurmrest;

import javax.ws.rs.WebApplicationException;
import java.net.MalformedURLException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.quiptiq.wurmrest.health.GameServiceHealthCheck;
import com.quiptiq.wurmrest.resources.PlayerResource;
import com.quiptiq.wurmrest.resources.Resource;
import com.quiptiq.wurmrest.resources.ServerResource;
import com.quiptiq.wurmrest.resources.VillageResource;
import com.quiptiq.wurmrest.rmi.*;
import io.dropwizard.Application;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
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
        bootstrap.addBundle(new SwaggerBundle<WurmRestConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration
                    (WurmRestConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
    }

    @Override
    public void run(WurmRestConfiguration wurmRestConfiguration, Environment environment) throws
            Exception {
        RmiProvider rmiProvider;
        try {
            rmiProvider = wurmRestConfiguration.getRmiProviderFactory().build();
        } catch (MalformedURLException e) {
            // Can't do anything if the URL is bad
            throw new WebApplicationException("Couldn't create service", e);
        }
        JerseyEnvironment env = environment.jersey();
        for (Resource resource : new Resource[]{
                new PlayerResource(new PlayerService(rmiProvider)),
                new VillageResource(new VillageService(rmiProvider)),
                new ServerResource(new AdminService(rmiProvider))
        }) {
            env.register(resource);
        }

        // Serialization/Deserialization of dates and times as ISO8601 with time zones
        environment.getObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .registerModule(new JavaTimeModule());

        final GameServiceHealthCheck healthCheck =
                new GameServiceHealthCheck(new RmiGameService(rmiProvider));
        environment.healthChecks().register("Game Service", healthCheck);
    }
}
