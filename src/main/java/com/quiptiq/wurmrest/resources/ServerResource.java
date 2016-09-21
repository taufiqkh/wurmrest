package com.quiptiq.wurmrest.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import java.time.ZonedDateTime;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Announcement;
import com.quiptiq.wurmrest.api.ServerStatus;
import com.quiptiq.wurmrest.api.ShutdownCommand;
import com.quiptiq.wurmrest.rmi.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * Exposes general server administration functions
 */
@Path(ServerResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = ServerResource.PATH, description = "General server administration functions")
public class ServerResource implements Resource {
    static final String PATH = "/server";

    private final AdminService service;

    public ServerResource(AdminService service) {
        this.service = service;
    }

    @GET
    @Path("/status")
    @ApiOperation(
            value = "Current status of the server",
            notes = "Determines whether or not the game service is contactable and is running and" +
                    " returns this status with a time stamp of the status check. If the server " +
                    "cannot be contacted, isRunning is null.",
            response = ServerStatus.class
    )
    public ServerStatus getStatus() {
        ZonedDateTime asAt = ZonedDateTime.now();
        try {
            Result<Boolean> result = service.isRunning();
            if (result.isSuccess()) {
                return new ServerStatus(result.getValue(), false, asAt);
            }
        } catch (WebApplicationException e) {
            // Do nothing, return null for isRunning and false for connected
        }
        return new ServerStatus(null, false, asAt);
    }

    @POST
    @Path("/broadcast")
    @ApiOperation(
            value = "Broadcasts an announcement to the server",
            notes = "Returns a result with a boolean with value true if the broadcast was " +
                    "successful, otherwise an error",
            response = Result.class
    )
    @ApiImplicitParam(name = "message", value = "Message to be broadcast")
    public Result<Boolean> announce(Announcement message) {
        return service.broadcast(message.getMessage());
    }

    @POST
    @Path("/shutdown")
    @ApiOperation(
            value = "Shuts down the server",
            notes = "Returns a result with a boolean with value true if the initiation of the " +
                    "shutdown was successful, otherwise an error",
            response = Result.class
    )
    public Result<Boolean> shutdown(ShutdownCommand command) {
        return service.startShutdown(
                command.getInstigator(),
                command.getTimer(),
                command.getReason());
    }
}
