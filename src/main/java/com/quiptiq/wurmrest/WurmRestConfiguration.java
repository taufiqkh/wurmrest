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
    @NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";

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

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }
}
