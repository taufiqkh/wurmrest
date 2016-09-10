package com.quiptiq.wurmrest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quiptiq.wurmrest.bank.BalanceResult;
import com.quiptiq.wurmrest.rmi.RmiGameService;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Resource representing the bank for all players.
 */
@Path("/bank")
@Produces(MediaType.APPLICATION_JSON)
public class BankResource {
    private final RmiGameService service;

    public BankResource(RmiGameService service) {
        this.service = service;
    }

    @GET
    public Result<BalanceResult> getBalance(@QueryParam("player") @NotEmpty String player) {
        return service.getBalance(player);
    }
}
