package com.quiptiq.wurmrest.api;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the Deeds collection representation
 */
public class DeedsTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private String jsonFixture(String target) {
        return fixture("fixtures/deeds/" + target + ".json");
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void doesNotEqualNull() {
        ArrayList<Deed> deedsList = new ArrayList<>();
        deedsList.add(new Deed(32, "Townsville"));
        Deeds deeds = new Deeds(deedsList);
        assertFalse(deeds.equals(null));
        assertFalse(new Deeds(0).equals(null));
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void equalsIsReflexive() {
        ArrayList<Deed> deedsList = new ArrayList<>();
        deedsList.add(new Deed(23, "Canberra"));
        Deeds deeds = new Deeds(deedsList);
        assertTrue(deeds.equals(deeds));
        deeds = new Deeds(0);
        assertTrue(deeds.equals(deeds));
    }

    @Test
    public void equalsIsAssociative() {
        ArrayList<Deed> deedsList = new ArrayList<>();
        deedsList.add(new Deed(56, "Rockhampton"));
        deedsList.add(new Deed(56, "Rockhampton"));
        deedsList.add(new Deed(45, "Bundaberg"));
        Deeds deeds1 = new Deeds(deedsList);
        deedsList = new ArrayList<>();
        deedsList.add(new Deed(56, "Rockhampton"));
        deedsList.add(new Deed(56, "Rockhampton"));
        deedsList.add(new Deed(45, "Bundaberg"));
        Deeds deeds2 = new Deeds(deedsList);
        deedsList = new ArrayList<>();
        deedsList.add(new Deed(56, "Rockhampton"));
        deedsList.add(new Deed(56, "Rockhampton"));
        deedsList.add(new Deed(47, "Sydney"));
        Deeds deeds3 = new Deeds(deedsList);
        assertTrue(deeds1.equals(deeds2));
        assertTrue(deeds2.equals(deeds1));
        assertFalse(deeds2.equals(deeds3));
        assertFalse(deeds3.equals(deeds2));
    }

    @Test
    public void serialiseToJson() throws Exception {
        final ArrayList<Deed> deedsList = new ArrayList<>();
        deedsList.add(new Deed(1, "Test"));
        deedsList.add(new Deed(49128, "Test 2"));
        Deeds deeds = new Deeds(4, deedsList.size(), deedsList);
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("deeds"), Deeds.class));
        assertEquals(expected, MAPPER.writeValueAsString(deeds));
    }

    @Test
    public void serialisesCountOnlyToJson() throws Exception {
        Deeds deeds = new Deeds(25);
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("deedsTotal"), Deeds.class));
        assertEquals(expected, MAPPER.writeValueAsString(deeds));
    }

    @Test
    public void serialisesEmptyDeedsToJson() throws Exception {
        Deeds deeds = new Deeds(new ArrayList<>());
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("deedsEmpty"), Deeds.class));
        assertEquals(expected, MAPPER.writeValueAsString(deeds));
    }

    @Test
    public void deserialisesFromJson() throws Exception {
        final ArrayList<Deed> deedsList = new ArrayList<>();
        deedsList.add(new Deed(1, "Test"));
        deedsList.add(new Deed(49128, "Test 2"));
        Deeds expected = new Deeds(4, deedsList.size(), deedsList);
        assertEquals(expected, MAPPER.readValue(jsonFixture("deeds"), Deeds.class));
    }

    @Test
    public void deserialisesTotalOnlyFromJson() throws Exception {
        Deeds expected = new Deeds(25);
        assertEquals(expected, MAPPER.readValue(jsonFixture("deedsTotal"), Deeds.class));
    }

    @Test
    public void deserialisesEmptyFromJson() throws Exception {
        Deeds expected = new Deeds(new ArrayList<>());
        assertEquals(expected, MAPPER.readValue(jsonFixture("deedsEmpty"), Deeds.class));
    }
}
