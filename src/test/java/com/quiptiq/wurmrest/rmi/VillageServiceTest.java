package com.quiptiq.wurmrest.rmi;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.TilePosition;
import com.quiptiq.wurmrest.api.Village;
import com.quiptiq.wurmrest.api.Villages;
import com.wurmonline.server.webinterface.WebInterface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
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

    private Map<String, ?> toMap(Village village) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("Name", village.getName());
        map.put("Deedid", village.getDeedId());
        map.put("Motto", village.getMotto());
        map.put("Location", village.getKingdom());
        map.put("Size", village.getSize());
        map.put("Founder", village.getFounder());
        map.put("Mayor", village.getMayor());
        map.put("Disbanding in", village.getReadableTimeUntilDisband());
        map.put("Disbander", village.getDisbander());
        map.put("Citizens", village.getCitizens());
        map.put("Allies", village.getAllies());
        map.put("guards", village.getGuards());
        map.put("Token coord x", village.getTokenPosition().getX());
        map.put("Token coord y", village.getTokenPosition().getY());
        return map;
    }

    @Test
    public void correctVillages() throws Exception {
        ArrayList<Village> villagesList = new ArrayList<>();
        Village brisbane = new Village(123, "Brisbane", 55L, "yay", "Australia", 23,
                "A founder", "A mayor", "10 seconds", "Disbandy Person", 12, 30, 40, new
                TilePosition(50, 60));
        Village sydney = new Village(456, "Sydney", 56L, "whee", "Australia", 32,
                "Founder2", "Mayor2", null, null, 21, 0, 0, new TilePosition(40, 10));
        villagesList.add(brisbane);
        villagesList.add(sydney);
        TreeMap<Integer, String> villagesMap = new TreeMap<>();
        for (Village village : villagesList) {
            villagesMap.put(village.getId(), village.getName());
        }
        when(webInterface.getDeeds(password)).thenReturn(villagesMap);
        doReturn(toMap(brisbane)).when(webInterface).getDeedSummary(password, brisbane.getId());
        doReturn(toMap(sydney)).when(webInterface).getDeedSummary(password, sydney.getId());
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
