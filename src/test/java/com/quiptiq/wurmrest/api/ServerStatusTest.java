package com.quiptiq.wurmrest.api;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;

/**
 * Test the serialisation and deserialisation of the {@link ServerStatus} representation.
 */
public class ServerStatusTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private String jsonFixture(String target) {
        return fixture("fixtures/status/" + target + ".json");
    }

    @Before
    public void setUp() {
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MAPPER.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        MAPPER.registerModule(new JavaTimeModule());
    }

    @Test
    public void serialisesToJson() throws Exception {
        ZonedDateTime time = ZonedDateTime
                .of(2000, 1, 31, 12, 43, 58, 654000000, ZoneId.of("UTC+10"));
        ServerStatus status = new ServerStatus(true, true, time);
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("isRunning"), ServerStatus.class));
        assertEquals(expected, MAPPER.writeValueAsString(status));
    }

    @Test
    public void notRunningSerialisesToJson() throws Exception {
        ZonedDateTime time = ZonedDateTime
                .of(2010, 12, 31, 10, 6, 12, 123000000, ZoneId.of("UTC-5"));
        ServerStatus status = new ServerStatus(false, true, time);
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("isNotRunning"), ServerStatus.class));
        assertEquals(expected, MAPPER.writeValueAsString(status));
    }

    @Test
    public void notConnectedSerialisesToJson() throws Exception {
        ZonedDateTime time = ZonedDateTime
                .of(2014, 6, 30, 5, 14, 32, 987000000, ZoneId.of("UTC"));
        ServerStatus status = new ServerStatus(null, false, time);
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("isNotConnected"), ServerStatus.class));
        assertEquals(expected, MAPPER.writeValueAsString(status));
    }

    @Test
    public void deserialisesFromJson() throws Exception {
        // We lose any UTC/GMT info, but it not currently important
        ZonedDateTime time = ZonedDateTime
                .of(2000, 1, 31, 12, 43, 58, 654000000, ZoneId.of("+10"));
        ServerStatus expected = new ServerStatus(true, true, time);
        ServerStatus status = MAPPER.readValue(jsonFixture("isRunning"), ServerStatus.class);
        assertEquals(expected.getTimeStamp(), status.getTimeStamp());
        assertEquals(expected.isConnected(), status.isConnected());
        assertEquals(expected.isRunning(), status.isRunning());
    }

    @Test
    public void deserialisesNotRunningFromJson() throws Exception {
        ZonedDateTime time = ZonedDateTime
                .of(2010, 12, 31, 10, 6, 12, 123000000, ZoneId.of("-5"));
        ServerStatus expected = new ServerStatus(false, true, time);
        ServerStatus status = MAPPER.readValue(jsonFixture("isNotRunning"), ServerStatus.class);
        assertEquals(expected.getTimeStamp(), status.getTimeStamp());
        assertEquals(expected.isConnected(), status.isConnected());
        assertEquals(expected.isRunning(), status.isRunning());
    }

    @Test
    public void deserialisesNotConnectedFromJson() throws Exception {
        ZonedDateTime time = ZonedDateTime
                .of(2014, 6, 30, 5, 14, 32, 987000000, ZoneId.of("Z"));
        ServerStatus expected = new ServerStatus(null, false, time);
        ServerStatus status = MAPPER.readValue(jsonFixture("isNotConnected"), ServerStatus.class);
        assertEquals(expected.getTimeStamp(), status.getTimeStamp());
        assertEquals(expected.isConnected(), status.isConnected());
        assertEquals(expected.isRunning(), status.isRunning());
    }
}
