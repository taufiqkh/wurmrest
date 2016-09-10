package com.quiptiq.wurmrest.rmi;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.net.MalformedURLException;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Creates RMI Providers from configuration
 */
public class RmiProviderFactory {
    @JsonProperty
    private String hostName = "localhost";

    @Min(1)
    @Max(65535)
    @JsonProperty
    private int port = 7220;

    @JsonProperty
    private String objectName = "WebInterface";

    @NotEmpty
    @JsonProperty
    private String password;

    public RmiProvider build() throws MalformedURLException {
        return new RmiProvider(hostName, port, objectName, password);
    }
}
