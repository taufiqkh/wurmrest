package com.quiptiq.wurmrest.resources;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Balance;
import com.quiptiq.wurmrest.api.Transaction;
import com.quiptiq.wurmrest.rmi.RmiGameService;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Resource representing the bank for all players.
 */
@Path("/bank")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BankResource {
    private final RmiGameService service;

    public BankResource(RmiGameService service) {
        this.service = service;
    }

    @GET
    @Path("/{player}/money")
    public Balance getBalance(@PathParam("player") @NotEmpty String player) {
        Result<Balance> result = service.getBalance(player);
        if (result.isError()) {
            // We don't currently differentiate between errors
            throw new WebApplicationException(
                    result.getError(), Response.Status.SERVICE_UNAVAILABLE);
        }
        return result.getValue();
    }

    @POST
    @Path("/{player}/money")
    public Result<String> addMoney(@PathParam("player") @NotEmpty String player,
                           Transaction transaction) {
        Result<String> result = service.addMoney(
                player, transaction.getAmount(), transaction.getDetails());
        /*
        if (result.isError()) {
            throw new WebApplicationException(result.getError(), Response.Status
                    .SERVICE_UNAVAILABLE);
        }*/
        return result;
    }
}
