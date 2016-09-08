package com.quiptiq.wurmrest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;
import com.quiptiq.wurmrest.bank.BalanceResult;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Resource representing the bank for all players.
 */
@Path("/bank")
@Produces(MediaType.APPLICATION_JSON)
public class BankResource {
    private final WurmService service;

    public BankResource(WurmService service) {
        this.service = service;
    }

    @GET
    @Timed
    public BalanceResult getBalance(@QueryParam("player") @NotEmpty String player) {
        return service.getBalance(player);
    }
}
