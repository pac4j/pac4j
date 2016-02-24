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
    
    private static final String MALE = "m";
    
    private static final String FEMALE = "f";
    
    private static final String UNSPECIFIED = "unspecified";
    
    private final GenderConverter converter = new GenderConverter(MALE, FEMALE);

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
        assertEquals(Gender.MALE, this.converter.convert(MALE));
    }

    @Test
    public void testFemale() {
        assertEquals(Gender.FEMALE, this.converter.convert(FEMALE));
    }

    @Test
    public void testUnspecified() {
        assertEquals(Gender.UNSPECIFIED, this.converter.convert(UNSPECIFIED));
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
