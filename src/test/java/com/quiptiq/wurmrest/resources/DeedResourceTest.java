package com.quiptiq.wurmrest.resources;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.ArrayList;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Deed;
import com.quiptiq.wurmrest.api.Deeds;
import com.quiptiq.wurmrest.rmi.DeedService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DeedResource}
 */
public class DeedResourceTest {
    private static final DeedService service = mock(DeedService.class);

    private static final ResourceTestHelper helper =
            new ResourceTestHelper(new DeedResource(service));

    @ClassRule
    public static final ResourceTestRule rule = helper.getResources();

    @Before
    public void setUp() {
        reset(service);
    }

    @Test
    public void correctDeedTotal() {
        int total = 123;
        when(service.getDeedsTotal()).thenReturn(Result.success(new Deeds(total)));
        Deeds deeds = helper.callGet("/deeds", Deeds.class, "filter", "total");
        assertEquals(total, deeds.getTotal());
        assertNull(deeds.getCount());
        assertNull(deeds.getDeeds());
    }

    @Test
    public void totalThrowOnBadFilter() {
        when(service.getDeedsTotal()).thenReturn(Result.success(new Deeds(321)));
        try {
            helper.callGet("/deeds", Deeds.class, "filter", "invalid");
        } catch (WebApplicationException e) {
            assertEquals(Response.Status.BAD_REQUEST, e.getResponse().getStatusInfo());
        }
    }

    @Test(expected = WebApplicationException.class)
    public void totalThrowOnError() {
        when(service.getDeedsTotal()).thenReturn(Result.error("wat"));
        helper.callGet("/deeds", Deeds.class, "filter", "total");
    }

    @Test(expected = WebApplicationException.class)
    public void totalPassOnException() {
        when(service.getDeedsTotal()).thenThrow(new WebApplicationException("snafu"));
        helper.callGet("/deeds", Deeds.class, "filter", "total");
    }

    @Test
    public void correctDeeds() {
        ArrayList<Deed> deedsList = new ArrayList<>();
        deedsList.add(new Deed(232, "Blackwater"));
        deedsList.add(new Deed(323, "Emerald"));
        Deeds expected = new Deeds(deedsList);
        when(service.getDeeds()).thenReturn(Result.success(expected));
        Deeds deeds = helper.callGet("/deeds", Deeds.class);
        assertEquals(expected, deeds);
    }

    @Test(expected = WebApplicationException.class)
    public void deedsThrowOnError() {
        when(service.getDeeds()).thenReturn(Result.error("wat"));
        helper.callGet("/deeds", Deeds.class);
    }

    @Test(expected = WebApplicationException.class)
    public void deedsPassOnException() {
        when(service.getDeeds()).thenThrow(new WebApplicationException("snafu"));
        helper.callGet("/deeds", Deeds.class);
    }
}
