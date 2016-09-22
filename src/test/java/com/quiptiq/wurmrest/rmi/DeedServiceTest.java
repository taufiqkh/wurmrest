package com.quiptiq.wurmrest.rmi;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Deed;
import com.quiptiq.wurmrest.api.Deeds;
import com.wurmonline.server.webinterface.WebInterface;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests for the DeedService service
 */
public class DeedServiceTest {
    private WebInterface webInterface;

    private final String password = "password";

    private DeedService service;

    @Before
    public void setUp() throws Exception {
        RmiServiceTestHelper helper = new RmiServiceTestHelper(password);
        webInterface = helper.getWebInterface();
        service = new DeedService(helper.getRmiProvider());
    }

    @Test
    public void correctDeedTotal() throws Exception {
        HashMap<Integer, String> expected = new HashMap<>();
        when(webInterface.getDeeds(password)).thenReturn(expected);
        Result<Deeds> result = service.getDeedsTotal();
        assertTrue(result.isSuccess());
        Deeds deeds = result.getValue();
        assertEquals(expected.size(), deeds.getTotal());
        assertNull(deeds.getCount());
        assertNull(deeds.getDeeds());
    }

    @Test(expected = WebApplicationException.class)
    public void errorDeedsTotalResult() throws Exception {
        when(webInterface.getDeeds(password)).thenThrow(new WebApplicationException("total"));
        service.getDeedsTotal();
    }

    @Test
    public void correctDeeds() throws Exception {
        ArrayList<Deed> deedsList = new ArrayList<>();
        deedsList.add(new Deed(123, "Brisbane"));
        deedsList.add(new Deed(456, "Sydney"));
        TreeMap<Integer, String> deedsMap = new TreeMap<>();
        for (Deed deed : deedsList) {
            deedsMap.put(deed.getId(), deed.getName());
        }
        when(webInterface.getDeeds(password)).thenReturn(deedsMap);
        Result<Deeds> result = service.getDeeds();
        assertTrue(result.isSuccess());
        Deeds deeds = result.getValue();
        assertEquals(new Deeds(deedsList), deeds);
    }

    @Test(expected = WebApplicationException.class)
    public void errorDeedsResult() throws Exception {
        when(webInterface.getDeeds(password)).thenThrow(new WebApplicationException("deeds"));
        service.getDeeds();
    }
}
