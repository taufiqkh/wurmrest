package com.quiptiq.wurmrest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for a single Deed representation
 */
public class DeedTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private String jsonFixture(String file) {
        return fixture("fixtures/deeds/" + file + ".json");
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void doesNotEqualNull() {
        Deed test = new Deed(1234, "Brisbane");
        assertFalse(test.equals(null));
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void equalsIsReflexive() {
        Deed test = new Deed(123, "Sydney");
        assertTrue(test.equals(test));
    }

    @Test
    public void equalsIsAssociative() {
        Deed test1a = new Deed(567, "Melbourne");
        Deed test1b = new Deed(567, "Melbourne");
        Deed test2 = new Deed(589, "Perth");
        assertTrue(test1a.equals(test1b));
        assertTrue(test1b.equals(test1a));
        assertFalse(test1a.equals(test2));
        assertFalse(test2.equals(test1a));
    }

    @Test
    public void allValuesMustBeEqual() {
        Deed test1 = new Deed(123, "Botany Bay");
        Deed test2 = new Deed(123, "Sydney");
        Deed test3 = new Deed(2931, "Sydney");
        assertFalse(test1.equals(test2));
        assertFalse(test2.equals(test3));
    }

    @Test
    public void isEqualMeansSameHashCode() {
        Deed test1 = new Deed(222, "Darwin");
        Deed test2 = new Deed(222, "Darwin");
        assertTrue(test1.equals(test2));
        assertEquals(test1.hashCode(), test2.hashCode());
    }

    @Test
    public void serialisesToJson() throws Exception {
        Deed deed = new Deed(1, "Hobart");
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(jsonFixture("deed"), Deed.class));
        assertEquals(expected, MAPPER.writeValueAsString(deed));
    }
}
