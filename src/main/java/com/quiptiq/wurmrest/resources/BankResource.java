package com.quiptiq.wurmrest.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Balance;
import com.quiptiq.wurmrest.api.Transaction;
import com.quiptiq.wurmrest.rmi.BankService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Resource representing the bank for all players.
 */
@Path(BankResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = BankResource.PATH, description="Financial services for all players and server accounts")
public class BankResource {
    static final String PATH = "/bank";

    private final BankService service;

    public BankResource(BankService service) {
        this.service = service;
    }

    @GET
    @Path("/{player}/money")
    @ApiOperation(
            value = "Retrieve the value for a player",
            notes = "Returns an error if the player cannot be found",
            response = Balance.class
    )
    @ApiResponse(code = 404, message = "Player not found")
    public Balance getBalance(@PathParam("player") @NotEmpty String player) {
        Result<Balance> result = service.getBalance(player);
        if (result.isError()) {
            throw new WebApplicationException(
                    result.getError(), Response.Status.NOT_FOUND);
        }
        return result.getValue();
    }

    @POST
    @Path("/{player}/money")
    @ApiOperation(
            value = "Add money to or remove money from a player's bank account",
            notes = "Returns a message indicating the result of the transaction",
            response = Result.class
    )
    @ApiResponse(code = 404, message = "Player not found")
    public Result<String> addMoney(@PathParam("player") @NotEmpty String player,
                           Transaction transaction) {
        Result<String> result = service.addMoney(
                player, transaction.getAmount(), transaction.getDetails());
        if (result.isError()) {
            throw new WebApplicationException(
                    result.getError(), Response.Status.NOT_FOUND);
        }
        return result;
    }
}
