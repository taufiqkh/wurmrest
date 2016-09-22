package com.quiptiq.wurmrest.resources;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.ArrayList;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Village;
import com.quiptiq.wurmrest.api.Villages;
import com.quiptiq.wurmrest.rmi.VillageService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static com.quiptiq.wurmrest.resources.VillageResource.PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link VillageResource}
 */
public class VillageResourceTest {
    private static final VillageService service = mock(VillageService.class);

    private static final ResourceTestHelper helper =
            new ResourceTestHelper(new VillageResource(service));

    @ClassRule
    public static final ResourceTestRule rule = helper.getResources();

    @Before
    public void setUp() {
        reset(service);
    }

    @Test
    public void correctVillageTotal() {
        int total = 123;
        when(service.getVillagesTotal()).thenReturn(Result.success(new Villages(total)));
        Villages villages = helper.callGet(PATH, Villages.class, "filter", "total");
        assertEquals(total, villages.getTotal());
        assertNull(villages.getCount());
        assertNull(villages.getVillages());
    }

    @Test
    public void totalThrowOnBadFilter() {
        when(service.getVillagesTotal()).thenReturn(Result.success(new Villages(321)));
        try {
            helper.callGet(PATH, Villages.class, "filter", "invalid");
        } catch (WebApplicationException e) {
            assertEquals(Response.Status.BAD_REQUEST, e.getResponse().getStatusInfo());
        }
    }

    @Test(expected = WebApplicationException.class)
    public void totalThrowOnError() {
        when(service.getVillagesTotal()).thenReturn(Result.error("wat"));
        helper.callGet(PATH, Villages.class, "filter", "total");
    }

    @Test(expected = WebApplicationException.class)
    public void totalPassOnException() {
        when(service.getVillagesTotal()).thenThrow(new WebApplicationException("snafu"));
        helper.callGet(PATH, Villages.class, "filter", "total");
    }

    @Test
    public void correctVillages() {
        ArrayList<Village> villagesList = new ArrayList<>();
        villagesList.add(new Village(232, "Blackwater"));
        villagesList.add(new Village(323, "Emerald"));
        Villages expected = new Villages(villagesList);
        when(service.getVillages()).thenReturn(Result.success(expected));
        Villages villages = helper.callGet("/villages", Villages.class);
        assertEquals(expected, villages);
    }

    @Test(expected = WebApplicationException.class)
    public void villagesThrowOnError() {
        when(service.getVillages()).thenReturn(Result.error("wat"));
        helper.callGet(PATH, Villages.class);
    }

    @Test(expected = WebApplicationException.class)
    public void villagesPassOnException() {
        when(service.getVillages()).thenThrow(new WebApplicationException("snafu"));
        helper.callGet(PATH, Villages.class);
    }
}
