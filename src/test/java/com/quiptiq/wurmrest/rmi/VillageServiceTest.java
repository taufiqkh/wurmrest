package com.quiptiq.wurmrest.rmi;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Village;
import com.quiptiq.wurmrest.api.Villages;
import com.wurmonline.server.webinterface.WebInterface;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests for the VillageService service
 */
public class VillageServiceTest {
    private WebInterface webInterface;

    private final String password = "password";

    private VillageService service;

    @Before
    public void setUp() throws Exception {
        RmiServiceTestHelper helper = new RmiServiceTestHelper(password);
        webInterface = helper.getWebInterface();
        service = new VillageService(helper.getRmiProvider());
    }

    @Test
    public void correctVillageTotal() throws Exception {
        HashMap<Integer, String> expected = new HashMap<>();
        when(webInterface.getDeeds(password)).thenReturn(expected);
        Result<Villages> result = service.getVillagesTotal();
        assertTrue(result.isSuccess());
        Villages villages = result.getValue();
        assertEquals(expected.size(), villages.getTotal());
        assertNull(villages.getCount());
        assertNull(villages.getVillages());
    }

    @Test(expected = WebApplicationException.class)
    public void errorVillagesTotalResult() throws Exception {
        when(webInterface.getDeeds(password)).thenThrow(new WebApplicationException("total"));
        service.getVillagesTotal();
    }

    @Test
    public void correctVillages() throws Exception {
        ArrayList<Village> villagesList = new ArrayList<>();
        villagesList.add(new Village(123, "Brisbane"));
        villagesList.add(new Village(456, "Sydney"));
        TreeMap<Integer, String> villagesMap = new TreeMap<>();
        for (Village village : villagesList) {
            villagesMap.put(village.getId(), village.getName());
        }
        when(webInterface.getDeeds(password)).thenReturn(villagesMap);
        Result<Villages> result = service.getVillages();
        assertTrue(result.isSuccess());
        Villages villages = result.getValue();
        assertEquals(new Villages(villagesList), villages);
    }

    @Test(expected = WebApplicationException.class)
    public void errorVillagesResult() throws Exception {
        when(webInterface.getDeeds(password)).thenThrow(new WebApplicationException("villages"));
        service.getVillages();
    }
}
