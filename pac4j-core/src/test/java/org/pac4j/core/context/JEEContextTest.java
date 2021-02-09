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

    private static final String CTX = "/ctx";
    private static final String PATH = "/path";
    private static final String CTX_PATH = "/ctx/path";

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
        var headerNames = new HashSet<String>();
        headerNames.add(KEY);
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(headerNames));
        when(request.getHeader(KEY)).thenReturn(VALUE);
        final var context = new JEEContext(request, response);
        assertEquals(VALUE, context.getRequestHeader(key).get());
    }

    @Test
    public void testGetPathNullFullPath() {
        when(request.getRequestURI()).thenReturn(null);
        final var context = new JEEContext(request, response);
        assertEquals("", context.getPath());
    }

    @Test
    public void testGetPathFullpath() {
        when(request.getRequestURI()).thenReturn(CTX_PATH);
        final var context = new JEEContext(request, response);
        assertEquals(CTX_PATH, context.getPath());
    }

    @Test
    public void testGetPathFullpathContext() {
        when(request.getRequestURI()).thenReturn(CTX_PATH);
        when(request.getContextPath()).thenReturn(CTX);
        final var context = new JEEContext(request, response);
        assertEquals(PATH, context.getPath());
    }

    @Test
    public void testGetPathDoubleSlashFullpathContext() {
        when(request.getRequestURI()).thenReturn("/" + CTX_PATH);
        when(request.getContextPath()).thenReturn(CTX);
        final var context = new JEEContext(request, response);
        assertEquals(PATH, context.getPath());
    }
}
