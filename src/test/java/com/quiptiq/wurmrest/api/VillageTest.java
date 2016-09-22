package com.quiptiq.wurmrest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for a single Village representation
 */
public class VillageTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private String jsonFixture(String file) {
        return fixture("fixtures/villages/" + file + ".json");
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void doesNotEqualNull() {
        Village test = new Village(1234, "Brisbane");
        assertFalse(test.equals(null));
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void equalsIsReflexive() {
        Village test = new Village(123, "Sydney");
        assertTrue(test.equals(test));
    }

    @Test
    public void equalsIsAssociative() {
        Village test1a = new Village(567, "Melbourne");
        Village test1b = new Village(567, "Melbourne");
        Village test2 = new Village(589, "Perth");
        assertTrue(test1a.equals(test1b));
        assertTrue(test1b.equals(test1a));
        assertFalse(test1a.equals(test2));
        assertFalse(test2.equals(test1a));
    }

    @Test
    public void allValuesMustBeEqual() {
        Village test1 = new Village(123, "Botany Bay");
        Village test2 = new Village(123, "Sydney");
        Village test3 = new Village(2931, "Sydney");
        assertFalse(test1.equals(test2));
        assertFalse(test2.equals(test3));
    }

    @Test
    public void isEqualMeansSameHashCode() {
        Village test1 = new Village(222, "Darwin");
        Village test2 = new Village(222, "Darwin");
        assertTrue(test1.equals(test2));
        assertEquals(test1.hashCode(), test2.hashCode());
    }

    @Test
    public void serialisesToJson() throws Exception {
        Village village = new Village(1, "Hobart");
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("villageMinimal"), Village.class));
        assertEquals(expected, MAPPER.writeValueAsString(village));
    }

    @Test
    public void serialisesAllToJson() throws Exception {
        Village village = new Village(2, "Adelaide", 23L, "Testing is fun", "Australia", 24,
                "Mr Adelaide", "Mayor Adelaide", "Some time", "disband person", 24, 321, 8,
                new TilePosition(23, 44));
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("village"), Village.class));
        assertEquals(expected, MAPPER.writeValueAsString(village));
    }
}
