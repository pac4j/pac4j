package org.pac4j.core.profile.converter;

import org.junit.Test;
import org.pac4j.core.profile.Gender;

import static org.junit.Assert.*;

/**
 * This class tests the {@link GenderConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class GenderConverterTests {

    private final GenderConverter converter = new GenderConverter();

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testMale() {
        assertEquals(Gender.MALE, this.converter.convert("m"));
    }

    @Test
    public void testFemale() {
        assertEquals(Gender.FEMALE, this.converter.convert("f"));
    }

    @Test
    public void testMaleNumber() {
        assertEquals(Gender.MALE, this.converter.convert(2));
    }

    @Test
    public void testFemaleNumber() {
        assertEquals(Gender.FEMALE, this.converter.convert(1));
    }

    @Test
    public void testUnspecified() {
        assertEquals(Gender.UNSPECIFIED, this.converter.convert("unspecified"));
    }

    @Test
    public void testMaleEnum() {
        assertEquals(Gender.MALE, this.converter.convert(Gender.MALE.toString()));
    }

    @Test
    public void testFemaleEnum() {
        assertEquals(Gender.FEMALE, this.converter.convert(Gender.FEMALE.toString()));
    }

    @Test
    public void testUnspecifiedEnum() {
        assertEquals(Gender.UNSPECIFIED, this.converter.convert(Gender.UNSPECIFIED.toString()));
    }
}
