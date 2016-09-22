package com.quiptiq.wurmrest.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Deeds;
import com.quiptiq.wurmrest.rmi.DeedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;

/**
 * Deed-specific information
 */
@Path(DeedResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = DeedResource.PATH, description = "Deed-specific information and actions")
public class DeedResource implements Resource {
    public static final String PATH = "/deeds";
    private final DeedService service;

    public DeedResource(DeedService service) {
        this.service = service;
    }

    @GET
    @ApiImplicitParam(name = "filter", value = "Filter for selecting deeds. Only \"total\" is " +
            "currently supported. \"total\" will return only the total count of deeds")
    public Deeds getDeeds(@QueryParam("filter") String filter) {
        Result<Deeds> result;
        if (filter == null) {
            result = service.getDeeds();
        } else if ("total".equals(filter)) {
            result = service.getDeedsTotal();
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
