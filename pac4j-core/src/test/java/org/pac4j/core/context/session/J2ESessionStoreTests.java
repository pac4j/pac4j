/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.core.context.session;

import org.junit.Test;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.TestsConstants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the {@link SessionStore} capability.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class J2ESessionStoreTests implements TestsConstants {

    @Test
    public void testDefaultSessionStore() {
        final J2EContext requestContext = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        assertTrue(requestContext.getSessionStore() instanceof J2ESessionStore);
        final J2EContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse());
        assertTrue(context.getSessionStore() instanceof J2ESessionStore);
    }

    @Test
    public void testMockSessionStoreJEContext() {
        final SessionStore store = mock(SessionStore.class);
        final J2EContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse(), store);
        context.getSessionIdentifier();
        context.getSessionAttribute(NAME);
        context.setSessionAttribute(NAME, VALUE);
        verify(store, times(1)).getOrCreateSessionId(any(WebContext.class));
        verify(store, times(1)).get(any(WebContext.class), eq(NAME));
        verify(store, times(1)).set(any(WebContext.class), eq(NAME), eq(VALUE));
    }

    @Test
    public void testMockSessionStoreJERequestContext() {
        final SessionStore store = mock(SessionStore.class);
        final J2EContext context = new J2EContext(new MockHttpServletRequest(), new MockHttpServletResponse(), store);
        context.getSessionIdentifier();
        context.getSessionAttribute(NAME);
        context.setSessionAttribute(NAME, VALUE);
        verify(store, times(1)).getOrCreateSessionId(any(WebContext.class));
        verify(store, times(1)).get(any(WebContext.class), eq(NAME));
        verify(store, times(1)).set(any(WebContext.class), eq(NAME), eq(VALUE));
    }
}
