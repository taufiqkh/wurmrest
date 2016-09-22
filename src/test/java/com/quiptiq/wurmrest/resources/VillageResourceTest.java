package com.quiptiq.wurmrest.resources;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Optional;

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
import static org.junit.Assert.assertNotNull;
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
        WebApplicationException caught = null;
        try {
            helper.callGet(PATH, Villages.class, "filter", "invalid");
        } catch (WebApplicationException e) {
            caught = e;
        }
        assertNotNull(caught);
        assertEquals(Response.Status.BAD_REQUEST, caught.getResponse().getStatusInfo());
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

    @Test
    public void villageById() {
        Village expected = new Village(1, "Test");
        when(service.getVillage(expected.getId())).thenReturn(
                Result.success(Optional.of(expected)));
        Village actual = helper.callGet(PATH + "/" + expected.getId(), Village.class);
        assertEquals(expected, actual);
    }

    @Test
    public void villageByIdNotFound() {
        Village test = new Village(1, "Test");
        int badId = test.getId() + 1;
        when(service.getVillage(test.getId())).thenReturn(Result.success(Optional.of(test)));
        when(service.getVillage(badId)).thenReturn(Result.success(Optional.empty()));
        WebApplicationException caught = null;
        try {
            helper.callGet(PATH + "/" + badId, Village.class);
        } catch (WebApplicationException e) {
            caught = e;
        }
        assertNotNull(caught);
        assertEquals(Response.Status.NOT_FOUND, caught.getResponse().getStatusInfo());
    }

    @Test
    public void villageByName() {
        Village expected = new Village(1, "Test");
        when(service.getVillage(expected.getName()))
                .thenReturn(Result.success(Optional.of(expected)));
        Village actual = helper.callGet(PATH + "/" + expected.getName(), Village.class);
        assertEquals(expected, actual);
    }

    @Test
    public void villageByNameNotFound() {
        Village test = new Village(1, "Test");
        String badName = test + "bad";
        when(service.getVillage(test.getName())).thenReturn(Result.success(Optional.of(test)));
        when(service.getVillage(badName)).thenReturn(Result.success(Optional.empty()));
        WebApplicationException caught = null;
        try {
            helper.callGet(PATH + "/" + badName, Village.class);
        } catch (WebApplicationException e) {
            caught = e;
        }
        assertNotNull(caught);
        assertEquals(Response.Status.NOT_FOUND, caught.getResponse().getStatusInfo());
    }
}
