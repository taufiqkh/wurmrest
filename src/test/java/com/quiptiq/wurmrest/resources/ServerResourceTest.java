package com.quiptiq.wurmrest.resources;

import com.quiptiq.wurmrest.RawResult;
import com.quiptiq.wurmrest.Result;
import com.quiptiq.wurmrest.api.Announcement;
import com.quiptiq.wurmrest.api.ShutdownCommand;
import com.quiptiq.wurmrest.rmi.AdminService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the server resource
 */
public class ServerResourceTest {
    private static final AdminService testService = mock(AdminService.class);

    private static final ResourceTestHelper helper =
            new ResourceTestHelper(new ServerResource(testService));

    @ClassRule
    public static final ResourceTestRule resources = helper.getResources();

    @After
    public void tearDown() {
        reset(testService);
    }

    @Test
    public void announce() {
        Announcement announcement = new Announcement("Test announcement");
        when(testService.broadcast(announcement.getMessage())).thenReturn(Result.success(true));
        RawResult result = helper.callPost("/server/broadcast", RawResult.class, announcement);
        assertNull(result.getError());
        assertTrue((Boolean) result.getValue());
    }

    @Test
    public void shutdown() {
        ShutdownCommand command = new ShutdownCommand("Test instigator", 600, "Do it");
        when(
                testService.startShutdown(
                        command.getInstigator(),
                        command.getTimer(),
                        command.getReason()))
                .thenReturn(Result.success(true));

        RawResult result = helper.callPost("/server/shutdown", RawResult.class, command);
        assertNull(result.getError());
        assertTrue((Boolean) result.getValue());
    }
}
