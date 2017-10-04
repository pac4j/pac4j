package org.pac4j.core.authorization.authorizer.csrf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.TestsConstants;

/**
 * Tests {@link CsrfAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class CsrfAuthorizerTests implements TestsConstants {

    private CsrfAuthorizer authorizer;

    @Before
    public void setUp() {
        authorizer = new CsrfAuthorizer();
        authorizer.setOnlyCheckPostRequest(false);
    }

    @Test
    public void testParameterOk() {
        final WebContext context = MockWebContext.create().addRequestParameter(Pac4jConstants.CSRF_TOKEN, VALUE)
                                    .addSessionAttribute(Pac4jConstants.CSRF_TOKEN, VALUE);
        Assert.assertTrue(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testParameterOkNewName() {
        final WebContext context = MockWebContext.create().addRequestParameter(NAME, VALUE)
                                    .addSessionAttribute(Pac4jConstants.CSRF_TOKEN, VALUE);
        authorizer.setParameterName(NAME);
        Assert.assertTrue(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testHeaderOk() {
        final WebContext context = MockWebContext.create().addRequestHeader(Pac4jConstants.CSRF_TOKEN, VALUE)
                                    .addSessionAttribute(Pac4jConstants.CSRF_TOKEN, VALUE);
        Assert.assertTrue(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testHeaderOkNewName() {
        final WebContext context = MockWebContext.create().addRequestHeader(NAME, VALUE)
                                    .addSessionAttribute(Pac4jConstants.CSRF_TOKEN, VALUE);
        authorizer.setHeaderName(NAME);
        Assert.assertTrue(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testNoToken() {
        final WebContext context = MockWebContext.create().addSessionAttribute(Pac4jConstants.CSRF_TOKEN, VALUE);
        Assert.assertFalse(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testNoTokenCheckAll() {
        final MockWebContext context = MockWebContext.create().addSessionAttribute(Pac4jConstants.CSRF_TOKEN, VALUE);
        authorizer.setOnlyCheckPostRequest(true);
        Assert.assertTrue(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testNoTokenPostRequest() {
        final MockWebContext context = MockWebContext.create().addSessionAttribute(Pac4jConstants.CSRF_TOKEN, VALUE);
        context.setRequestMethod(HttpConstants.HTTP_METHOD.POST.name());
        Assert.assertFalse(authorizer.isAuthorized(context, null));
    }

    @Test
    public void testHeaderOkButNoTokenInSession() {
        final WebContext context = MockWebContext.create().addRequestHeader(Pac4jConstants.CSRF_TOKEN, VALUE);
        Assert.assertFalse(authorizer.isAuthorized(context, null));
    }
}
