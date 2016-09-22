package com.quiptiq.wurmrest.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Villages;
import com.quiptiq.wurmrest.rmi.VillageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * Village-specific information
 */
@Path(VillageResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = VillageResource.PATH, description = "Village-specific information and actions")
public class VillageResource implements Resource {
    static final String PATH = "/villages";
    private final VillageService service;

    public VillageResource(VillageService service) {
        this.service = service;
    }

    @GET
    @ApiOperation(
            value = "Villages on the server",
            notes = "All villages for the server, with all details if no filter is applied. If " +
                    "filter is specified with a value of \"total\", only a total count is " +
                    "returned. Not other filter values are currently supported.",
            response = Villages.class
    )
    @ApiImplicitParam(name = "filter", value = "Optional filter for selecting villages. Only " +
            "\"total\" is currently supported. \"total\" will return only the total count of " +
            "villages")
    public Villages getVillages(@QueryParam("filter") String filter) {
        Result<Villages> result;
        if (filter == null) {
            result = service.getVillages();
        } else if ("total".equals(filter)) {
            result = service.getVillagesTotal();
        } else {
            throw new WebApplicationException("Invalid filter: " + filter,
                    Response.Status.BAD_REQUEST);
        }
        if (result.isError()) {
            throw new WebApplicationException(result.getError(), Response.Status.BAD_GATEWAY);
        }
        return result.getValue();
    }
}
