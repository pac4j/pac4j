package org.pac4j.core.context;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;
import org.springframework.mock.web.MockHttpServletResponse;

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

    @Test
    public void testCookie() {
        HttpServletResponse mockResponse = new MockHttpServletResponse();
        final var context = new JEEContext(request, mockResponse);
        Cookie c = new Cookie("thename","thevalue");
        context.addResponseCookie(c);
        assertEquals("thename=thevalue; Path=/; SameSite=Lax", mockResponse.getHeader("Set-Cookie"));
    }

    @Test
    public void testCookieNone() {
        HttpServletResponse mockResponse = new MockHttpServletResponse();
        final var context = new JEEContext(request, mockResponse);
        Cookie c = new Cookie("thename","thevalue");
        c.setSameSitePolicy("NONE");
        context.addResponseCookie(c);
        assertEquals("thename=thevalue; Path=/; Secure; SameSite=None", mockResponse.getHeader("Set-Cookie"));
    }

    @Test
    public void testCookieSecureStrict() {
        HttpServletResponse mockResponse = new MockHttpServletResponse();
        final var context = new JEEContext(request, mockResponse);
        Cookie c = new Cookie("thename","thevalue");
        c.setSameSitePolicy("strict");
        c.setSecure(true);
        context.addResponseCookie(c);
        assertEquals("thename=thevalue; Path=/; Secure; SameSite=Strict", mockResponse.getHeader("Set-Cookie"));
    }

    @Test
    public void testCookieExpires() {
        var mockResponse = new MockHttpServletResponse();
        var context = new JEEContext(request, mockResponse);
        Cookie c = new Cookie("thename","thevalue");
        c.setMaxAge(1000);
        context.addResponseCookie(c);

        var header = mockResponse.getHeader("Set-Cookie");
        assertNotNull(header);
        assertTrue(header.matches("thename=thevalue; Path=/; Max-Age=1000; Expires=.* GMT; SameSite=Lax"));
    }


}
