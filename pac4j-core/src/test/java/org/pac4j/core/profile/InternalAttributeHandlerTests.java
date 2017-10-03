package org.pac4j.core.profile;

import org.junit.Test;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.util.TestsConstants;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Tests {@link InternalAttributeHandler}.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public final class InternalAttributeHandlerTests implements TestsConstants {

    private static final boolean BOOL = true;
    private static final int INT = 1;
    private static final long LONG = 2L;
    private static final Date DATE = new Date();
    private static final URI URL;
    private static final Color COLOR = new Color(1,1,1);

    static {
        try {
            URL = new URI("http://www.google.com");
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void noStringify() {
        final InternalAttributeHandler handler = new InternalAttributeHandler();
        assertAttribute(handler, null, null);
        assertAttribute(handler, VALUE, VALUE);
        assertAttribute(handler, BOOL, BOOL);
        assertAttribute(handler, INT, INT);
        assertAttribute(handler, LONG, LONG);
        assertAttribute(handler, DATE, DATE);
        assertAttribute(handler, URL, URL);
        assertAttribute(handler, COLOR, COLOR);
    }

    @Test
    public void stringify() {
        final InternalAttributeHandler handler = new InternalAttributeHandler();
        handler.setStringify(true);
        assertAttribute(handler, null, null);
        assertAttribute(handler, VALUE, VALUE);
        assertAttribute(handler, BOOL, InternalAttributeHandler.PREFIX_BOOLEAN + BOOL);
        assertAttribute(handler, INT, InternalAttributeHandler.PREFIX_INT + INT);
        assertAttribute(handler, LONG, InternalAttributeHandler.PREFIX_LONG + LONG);
        assertAttribute(handler, DATE, InternalAttributeHandler.PREFIX_DATE
            + new SimpleDateFormat(Converters.DATE_TZ_GENERAL_FORMAT).format(DATE));
        assertAttribute(handler, URL, InternalAttributeHandler.PREFIX_URI + URL.toString());
        assertAttribute(handler, COLOR, InternalAttributeHandler.PREFIX_SB64
            + "rO0ABXNyABxvcmcucGFjNGouY29yZS5wcm9maWxlLkNvbG9y/5w8lvR27osCAANJAARibHVlSQAFZ3JlZW5JAANyZWR4cAAAAAEAAAABAAAAAQ==");
    }

    private void assertAttribute(final InternalAttributeHandler handler, final Object given, final Object transformed) {
        assertEquals(transformed, handler.prepare(given));
        if (given instanceof Date || given instanceof Color) {
            assertEquals(given.toString(), handler.restore(transformed).toString());
        } else {
            assertEquals(given, handler.restore(transformed));
        }
    }
}
