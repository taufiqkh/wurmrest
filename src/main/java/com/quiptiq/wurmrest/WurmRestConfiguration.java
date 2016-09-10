package com.quiptiq.wurmrest;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.quiptiq.wurmrest.rmi.RmiProviderFactory;
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;


/**
 * Encapsulates DropWizard Configuration
 */
public class WurmRestConfiguration extends Configuration {
    @NotNull
    @Valid
    private RmiProviderFactory rmiProviderFactory = new RmiProviderFactory();

    @JsonProperty("rmi")
    public void setRmiProviderFactory(RmiProviderFactory rmiProviderFactory) {
        this.rmiProviderFactory = rmiProviderFactory;
    }

    @JsonProperty("rmi")
    public RmiProviderFactory getRmiProviderFactory() {
        return rmiProviderFactory;
    }

}
