package com.quiptiq.wurmrest.api;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Tests the JSON conversions of the Players representation
 */
public class PlayersTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private String jsonFixture(String target) {
        return fixture("fixtures/players/" + target + ".json");
    }

    @Test
    public void serialiseToJson() throws Exception {
        final ArrayList<String> names = new ArrayList<>();
        names.add("Tom");
        names.add("Dave");
        Players players = new Players(names);
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("players"), Players.class));
        assertEquals(expected, MAPPER.writeValueAsString(players));
    }

    @Test
    public void serialisesCountOnlyToJson() throws Exception {
        final Players players = new Players(20);
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue("{\"count\":20}", Players.class));
        assertEquals(expected, MAPPER.writeValueAsString(players));
    }

    @Test
    public void serialisesEmptyNames() throws Exception {
        final Players players = new Players(new ArrayList<>());
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("playersEmpty"), Players.class));
        assertEquals(expected, MAPPER.writeValueAsString(players));
    }

    @Test
    public void deserialisesFromJson() throws Exception {
        ArrayList<String> names = new ArrayList<>();
        names.add("Tom");
        names.add("Dave");
        final Players expectedPlayers = new Players(names);
        Players players = MAPPER.readValue(jsonFixture("players"), Players.class);
        assertEquals(expectedPlayers.getCount(), players.getCount());
        assertThat(expectedPlayers.getPlayers(), is(players.getPlayers()));
    }

    @Test
    public void deserialisesCountOnlyFromJson() throws Exception {
        final Players expectedPlayers = new Players(10);
        Players players = MAPPER.readValue("{\"count\":10}", Players.class);
        assertEquals(expectedPlayers.getCount(), players.getCount());
        assertNull(players.getPlayers());
    }

    @Test
    public void deserialisesEmptyNames() throws Exception {
        final Players expectedPlayers = new Players(new ArrayList<>());
        Players players = MAPPER.readValue(jsonFixture("playersEmpty"), Players.class);
        assertEquals(expectedPlayers.getCount(), players.getCount());
        assertNotNull(players.getPlayers());
        assertThat(expectedPlayers.getPlayers(), is(players.getPlayers()));
    }

    @Test(expected = JsonMappingException.class)
    public void errorsOnMiscountDeserialization() throws Exception {
        MAPPER.readValue(jsonFixture("playersInvalid"), Players.class);
    }
}
