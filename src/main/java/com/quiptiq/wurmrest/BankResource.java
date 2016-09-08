package com.quiptiq.wurmrest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.quiptiq.wurmrest.bank.Balance;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Resource representing the bank for all players.
 */
@Path("/bank")
@Produces(MediaType.APPLICATION_JSON)
public class BankResource {
    public BankResource() {
    }

    @GET
    @Timed
    public Balance getBalance(@QueryParam("player") @NotEmpty String player) {
        return new Balance(player, 0);
    }
}
