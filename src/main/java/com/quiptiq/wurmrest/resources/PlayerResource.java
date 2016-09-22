package com.quiptiq.wurmrest.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Balance;
import com.quiptiq.wurmrest.api.Players;
import com.quiptiq.wurmrest.api.Transaction;
import com.quiptiq.wurmrest.rmi.PlayerService;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Player-specific details and administration
 */
@Path(PlayerResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PlayerResource.PATH, description = "Player-specific details and administration")
public class PlayerResource implements Resource {
    static final String PATH = "/players";

    private final PlayerService playerService;

    public PlayerResource(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GET
    @ApiOperation(
            value = "Players in the server cluster",
            notes = "All players known to the server cluster. The filter parameter may be " +
                    "specified to retrieve the total count (filter=total)",
            response = Players.class
    )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filter", value = "Filters the result set. Specifying total " +
                    "will return just the total count", dataType = "string"),
    })
    public Players getPlayers(@QueryParam("filter") String filter) {
        if (filter == null) {
            throw new WebApplicationException("Empty filter and village not supported",
                    Response.Status.BAD_REQUEST);
        }
        if ("total".equals(filter)) {
            Result<Integer> playerCount = playerService.getPlayerCount();
            if (playerCount.isSuccess()) {
                return new Players(playerCount.getValue());
            }
            throw new WebApplicationException(playerCount.getError(), Response.Status.BAD_GATEWAY);
        }
        throw new WebApplicationException("Invalid filter: " + filter, Response.Status.BAD_REQUEST);
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
        Result<Balance> result = playerService.getBalance(player);
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
        Result<String> result = playerService.addMoney(
                player, transaction.getAmount(), transaction.getDetails());
        if (result.isError()) {
            throw new WebApplicationException(
                    result.getError(), Response.Status.NOT_FOUND);
        }
        return result;
    }
}
