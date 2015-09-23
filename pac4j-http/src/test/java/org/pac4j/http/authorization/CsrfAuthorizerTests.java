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
package org.pac4j.http.authorization;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * Tests {@link CsrfAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class CsrfAuthorizerTests implements TestsConstants {

    @Test
    public void testParameterOk() {
        final WebContext context = MockWebContext.create().addRequestParameter(Pac4jConstants.CSRK_TOKEN, VALUE).addSessionAttribute(Pac4jConstants.CSRK_TOKEN, VALUE);
        assertTrue(new CsrfAuthorizer().isAuthorized(context, null));
    }

    @Test
    public void testParameterOkNewName() {
        final WebContext context = MockWebContext.create().addRequestParameter(NAME, VALUE).addSessionAttribute(Pac4jConstants.CSRK_TOKEN, VALUE);
        final CsrfAuthorizer authorizer = new CsrfAuthorizer();
        authorizer.setParameterName(NAME);
        assertTrue(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testHeaderOk() {
        final WebContext context = MockWebContext.create().addRequestHeader(Pac4jConstants.CSRK_TOKEN, VALUE).addSessionAttribute(Pac4jConstants.CSRK_TOKEN, VALUE);
        assertTrue(new CsrfAuthorizer().isAuthorized(context, null));
    }

    @Test
    public void testHeaderOkNewName() {
        final WebContext context = MockWebContext.create().addRequestHeader(NAME, VALUE).addSessionAttribute(Pac4jConstants.CSRK_TOKEN, VALUE);
        final CsrfAuthorizer authorizer = new CsrfAuthorizer();
        authorizer.setHeaderName(NAME);
        assertTrue(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testNoToken() {
        final WebContext context = MockWebContext.create().addSessionAttribute(Pac4jConstants.CSRK_TOKEN, VALUE);
        assertFalse(new CsrfAuthorizer().isAuthorized(context, null));
    }
}
