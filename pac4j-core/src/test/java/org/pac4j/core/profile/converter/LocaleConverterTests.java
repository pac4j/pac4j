package org.pac4j.core.profile.converter;

import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class tests the {@link org.pac4j.core.profile.converter.LocaleConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class LocaleConverterTests {

    private final LocaleConverter converter = new LocaleConverter();

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testLanguage() {
        final var locale = (Locale) this.converter.convert("fr");
        assertEquals("fr", locale.getLanguage());
    }

    @Test
    public void testLanguageCountry() {
        final var locale = (Locale) this.converter.convert(Locale.FRANCE.toString());
        assertEquals(Locale.FRANCE.getLanguage(), locale.getLanguage());
        assertEquals(Locale.FRANCE.getCountry(), locale.getCountry());
    }

    @Test
    public void testBadLocale() {
        assertNull(this.converter.convert("1_2_3"));
    }
}
