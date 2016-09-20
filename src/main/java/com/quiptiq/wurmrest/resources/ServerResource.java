package com.quiptiq.wurmrest.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Announcement;
import com.quiptiq.wurmrest.api.ShutdownCommand;
import com.quiptiq.wurmrest.rmi.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * Exposes general server administration functions
 */
@Path(ServerResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = ServerResource.PATH, description = "General server administration functions")
public class ServerResource {
    static final String PATH = "/server";

    private final AdminService service;

    public ServerResource(AdminService service) {
        this.service = service;
    }

    @GET
    @Path("/status/running")
    @ApiOperation(
            value = "Whether or not the service is running",
            notes = "Determines whether or not the game service is running and returns a result" +
                    " with a boolean value",
            response = Result.class
    )
    public Result<Boolean> isRunning() {
        return service.isRunning();
    }

    @POST
    @Path("/broadcast")
    @ApiOperation(
            value = "Broadcasts an announcement to the server",
            notes = "Returns a result with a boolean with value true if the broadcast was " +
                    "successful, otherwise an error",
            response = Result.class
    )
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
