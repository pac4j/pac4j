package org.pac4j.core.context;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.HashSet;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Tests {@link JEEContext}.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public final class JEEContextTest implements TestsConstants {

    private HttpServletRequest request;

    private HttpServletResponse response;

    @Before
    public void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void testGetHeaderNameMatches() {
        internalTestGetHeader("kEy");
    }

    @Test
    public void testGetHeaderNameStriclyMatches() {
        internalTestGetHeader(KEY);
    }

    private void internalTestGetHeader(final String key) {
        final HashSet<String> headerNames = new HashSet<>();
        headerNames.add(KEY);
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(headerNames));
        when(request.getHeader(KEY)).thenReturn(VALUE);
        final JEEContext context = new JEEContext(request, response);
        assertEquals(VALUE, context.getRequestHeader(key).get());
    }
}
