package com.quiptiq.wurmrest.resources;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.ResultDeserializer;
import io.dropwizard.testing.junit.ResourceTestRule;

/**
 * Performs resource-related helper functions
 */
class ResourceTestHelper {
    private final ResourceTestRule resources;

    ResourceTestHelper(Resource resource) {
        SimpleModule module = new SimpleModule().addDeserializer(Result.class,
                new ResultDeserializer());
        ObjectMapper mapper = new ObjectMapper().registerModule(module);
        resources = ResourceTestRule.builder()
                .setMapper(mapper)
                .addResource(resource)
                .build();
    }

    ResourceTestRule getResources() {
        return resources;
    }

    <R> R callGet(String path, Class<R> returnClass) {
        return resources.client().target(path).request().get(returnClass);
    }

    <R> R callGet(String path, Class<R> returnClass, String queryParam, Object... params) {
        return resources
                .client()
                .target(path)
                .queryParam(queryParam, params)
                .request()
                .get(returnClass);
    }

    <R, T> R callPost(String path, GenericType<R> returnType, T t) {
        WebTarget target = resources.client().target(path);
        Response response = target.request().post(Entity.json(t));
        return response.readEntity(returnType);
    }

}
