package com.quiptiq.wurmrest.resources;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import io.dropwizard.testing.junit.ResourceTestRule;

/**
 * Performs resource-related helper functions
 */
public class ResourceTestHelper {
    private final ResourceTestRule resources;
    public ResourceTestHelper(Object resource) {
        resources = ResourceTestRule.builder().addResource(resource).build();
    }

    public ResourceTestRule getResources() {
        return resources;
    }

    public <R> R callGet(String path, Class<R> returnClass) {
        return resources.client().target(path).request().get(returnClass);
    }

    public <R, T> R callPost(String path, Class<R> returnClass, T t) {
        WebTarget target = resources.client().target(path);
        Response response = target.request().post(Entity.json(t));
        return response.readEntity(returnClass);
    }

}
