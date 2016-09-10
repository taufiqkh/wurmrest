package com.quiptiq.wurmrest;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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

    @NotEmpty
    private String hostName = "localhost";

    @Min(1)
    @Max(65535)
    private int port = 7220;

    @NotEmpty
    private String rmiName = "WebInterface";

    @JsonProperty
    public String getHostName() {
        return hostName;
    }

    @JsonProperty
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @JsonProperty
    public int getPort() {
        return port;
    }

    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }

    @JsonProperty
    public String getRmiName() {
        return rmiName;
    }

    @JsonProperty
    public void setRmiName(String rmiName) {
        this.rmiName = rmiName;
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
