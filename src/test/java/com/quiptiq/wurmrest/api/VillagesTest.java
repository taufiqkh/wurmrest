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
 * Tests for the Villages collection representation
 */
public class VillagesTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private String jsonFixture(String target) {
        return fixture("fixtures/villages/" + target + ".json");
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void doesNotEqualNull() {
        ArrayList<Village> villagesList = new ArrayList<>();
        villagesList.add(new Village(32, "Townsville"));
        Villages villages = new Villages(villagesList);
        assertFalse(villages.equals(null));
        assertFalse(new Villages(0).equals(null));
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void equalsIsReflexive() {
        ArrayList<Village> villagesList = new ArrayList<>();
        villagesList.add(new Village(23, "Canberra"));
        Villages villages = new Villages(villagesList);
        assertTrue(villages.equals(villages));
        villages = new Villages(0);
        assertTrue(villages.equals(villages));
    }

    @Test
    public void equalsIsAssociative() {
        ArrayList<Village> villagesList = new ArrayList<>();
        villagesList.add(new Village(56, "Rockhampton"));
        villagesList.add(new Village(56, "Rockhampton"));
        villagesList.add(new Village(45, "Bundaberg"));
        Villages villages1 = new Villages(villagesList);
        villagesList = new ArrayList<>();
        villagesList.add(new Village(56, "Rockhampton"));
        villagesList.add(new Village(56, "Rockhampton"));
        villagesList.add(new Village(45, "Bundaberg"));
        Villages villages2 = new Villages(villagesList);
        villagesList = new ArrayList<>();
        villagesList.add(new Village(56, "Rockhampton"));
        villagesList.add(new Village(56, "Rockhampton"));
        villagesList.add(new Village(47, "Sydney"));
        Villages villages3 = new Villages(villagesList);
        assertTrue(villages1.equals(villages2));
        assertTrue(villages2.equals(villages1));
        assertFalse(villages2.equals(villages3));
        assertFalse(villages3.equals(villages2));
    }

    @Test
    public void serialiseToJson() throws Exception {
        final ArrayList<Village> villagesList = new ArrayList<>();
        villagesList.add(new Village(1, "Test"));
        villagesList.add(new Village(49128, "Test 2"));
        Villages villages = new Villages(4, villagesList.size(), villagesList);
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("villages"), Villages.class));
        assertEquals(expected, MAPPER.writeValueAsString(villages));
    }

    @Test
    public void serialisesCountOnlyToJson() throws Exception {
        Villages villages = new Villages(25);
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("villagesTotal"), Villages.class));
        assertEquals(expected, MAPPER.writeValueAsString(villages));
    }

    @Test
    public void serialisesEmptyvillagesToJson() throws Exception {
        Villages villages = new Villages(new ArrayList<>());
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("villagesEmpty"), Villages.class));
        assertEquals(expected, MAPPER.writeValueAsString(villages));
    }

    @Test
    public void deserialisesFromJson() throws Exception {
        final ArrayList<Village> villagesList = new ArrayList<>();
        villagesList.add(new Village(1, "Test"));
        villagesList.add(new Village(49128, "Test 2"));
        Villages expected = new Villages(4, villagesList.size(), villagesList);
        assertEquals(expected, MAPPER.readValue(jsonFixture("villages"), Villages.class));
    }

    @Test
    public void deserialisesTotalOnlyFromJson() throws Exception {
        Villages expected = new Villages(25);
        assertEquals(expected, MAPPER.readValue(jsonFixture("villagesTotal"), Villages.class));
    }

    @Test
    public void deserialisesEmptyFromJson() throws Exception {
        Villages expected = new Villages(new ArrayList<>());
        assertEquals(expected, MAPPER.readValue(jsonFixture("villagesEmpty"), Villages.class));
    }
}
