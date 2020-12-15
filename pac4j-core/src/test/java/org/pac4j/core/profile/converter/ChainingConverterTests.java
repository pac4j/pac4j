package org.pac4j.core.profile.converter;

import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;

/**
 * This class tests the {@link ChainingConverterTests} class.
 *
 * @author Misagh Moayyed
 * @since 4.3.0
 */
public final class ChainingConverterTests {

    @Test
    public void testChain() {
        ChainingConverter chain = new ChainingConverter(List.of(Converters.STRING, Converters.LOCALE));
        assertNotNull(chain.convert("english"));
        assertNotNull(chain.convert(List.of("english")));
        assertNotNull(chain.convert(Locale.ENGLISH));
        assertNotNull(chain.convert(List.of(Locale.ENGLISH)));
    }
}
