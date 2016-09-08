package com.quiptiq.wurmrest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.rmi.RemoteException;

import com.codahale.metrics.annotation.Timed;
import com.quiptiq.wurmrest.bank.Balance;
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
    public Balance getBalance(@QueryParam("player") @NotEmpty String player) {
        try {
            return new Balance(service.getBalance(player));
        } catch (RemoteException e) {
            throw new WebApplicationException("Could not get balance", e);
        }
    }
}
