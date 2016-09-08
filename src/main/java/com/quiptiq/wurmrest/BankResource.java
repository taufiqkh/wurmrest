package com.quiptiq.wurmrest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quiptiq.wurmrest.bank.BalanceResult;
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
    public BalanceResult getBalance(@QueryParam("player") @NotEmpty String player) {
        BalanceResult result = service.getBalance(player);
        if (result.getErrorType() == BalanceResult.BalanceResultType.BAD_PLAYER_ID) {
            throw new WebApplicationException(result.getError().orElse("No player found with " +
                    "the name " + player), Response.Status.NOT_FOUND);
        } else if (result.getErrorType() != BalanceResult.BalanceResultType.OK) {
            throw new WebApplicationException(result.getError().orElse("Unexpected error"),
                    Response.Status.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
}
