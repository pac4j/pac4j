package org.pac4j.core.profile;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * Tests the {@link AttributesDefinition}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class AttributesDefinitionTests implements TestsConstants {

    private AttributesDefinition definition;

    @Before
    public void setUp() {
        definition = new AttributesDefinition();
    }

    @Test
    public void testNoConverter() {
        assertEquals(VALUE, definition.convert(NAME, VALUE));
    }

    @Test
    public void testConverterPrimary() {
        definition.primary(NAME, v -> { return FAKE_VALUE; });
        assertEquals(NAME, definition.getPrimaryAttributes().get(0));
        assertEquals(FAKE_VALUE, definition.convert(NAME, VALUE));
    }

    @Test
    public void testConverterSecondary() {
        definition.secondary(NAME, v -> { return FAKE_VALUE; });
        assertEquals(NAME, definition.getSecondaryAttributes().get(0));
        assertEquals(FAKE_VALUE, definition.convert(NAME, VALUE));
    }
}
