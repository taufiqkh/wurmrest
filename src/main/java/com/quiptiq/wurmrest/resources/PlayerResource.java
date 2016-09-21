package com.quiptiq.wurmrest.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Players;
import com.quiptiq.wurmrest.rmi.PlayerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

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
            throw new WebApplicationException("Empty filter and deed not supported",
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
}
