package com.quiptiq.wurmrest.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.bank.Balance;
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
    @Path("/{player}/balance")
    public Balance getBalance(@PathParam("player") @NotEmpty String player) {
        Result<Balance> result = service.getBalance(player);
        if (result.isError()) {
            // We don't currently differentiate between errors
            throw new WebApplicationException(
                    result.getError(), Response.Status.SERVICE_UNAVAILABLE);
        }
        return result.getValue();
    }
}
