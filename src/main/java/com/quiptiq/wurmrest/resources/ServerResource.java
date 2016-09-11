package com.quiptiq.wurmrest.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Announcement;
import com.quiptiq.wurmrest.api.ShutdownCommand;
import com.quiptiq.wurmrest.rmi.AdminService;

/**
 * Exposes general server administration functions
 */
@Path("/server")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerResource {
    private final AdminService service;

    public ServerResource(AdminService service) {
        this.service = service;
    }

    @GET
    @Path("/status/running")
    public Result<Boolean> isRunning() {
        return service.isRunning();
    }

    @POST
    @Path("/broadcast")
    public Result<Boolean> announce(Announcement message) {
        return service.broadcast(message.getMessage());
    }

    @POST
    @Path("/shutdown")
    public Result<Boolean> shutdown(ShutdownCommand command) {
        return service.startShutdown(
                command.getInstigator(),
                command.getTimer(),
                command.getReason());
    }
}
