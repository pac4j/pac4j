package org.pac4j.core.util;

import org.junit.After;
import org.junit.Test;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.*;

import static org.junit.Assert.*;

/**
 * Tests {@link HttpActionHelper}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public final class HttpActionHelperTest implements TestsConstants {

    @After
    public void after() {
        HttpActionHelper.setAlwaysUse401ForUnauthenticated(true);
        HttpActionHelper.setUseModernHttpCodes(true);
    }

    @Test
    public void testRedirectUrlAfterGet() {
        HttpActionHelper.setUseModernHttpCodes(true);
        final var action = HttpActionHelper.buildRedirectUrlAction(MockWebContext.create(), PAC4J_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(PAC4J_URL, ((FoundAction) action).getLocation());
    }

    @Test
    public void testRedirectUrlAfterPost() {
        HttpActionHelper.setUseModernHttpCodes(true);
        final var action = HttpActionHelper
            .buildRedirectUrlAction(MockWebContext.create().setRequestMethod("POST"), PAC4J_URL);
        assertTrue(action instanceof SeeOtherAction);
        assertEquals(PAC4J_URL, ((SeeOtherAction) action).getLocation());
    }

    @Test
    public void testRedirectUrlAfterPostWithoutModernCode() {
        HttpActionHelper.setUseModernHttpCodes(false);
        final var action = HttpActionHelper
            .buildRedirectUrlAction(MockWebContext.create().setRequestMethod("POST"), PAC4J_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(PAC4J_URL, ((FoundAction) action).getLocation());
    }

    @Test
    public void testFormPostContentAfterGet() {
        HttpActionHelper.setUseModernHttpCodes(true);
        final var action = HttpActionHelper.buildFormPostContentAction(MockWebContext.create(), VALUE);
        assertTrue(action instanceof OkAction);
        assertEquals(VALUE, ((OkAction) action).getContent());
    }

    @Test
    public void testFormPostContentAfterPost() {
        HttpActionHelper.setUseModernHttpCodes(true);
        final var action = HttpActionHelper
            .buildFormPostContentAction(MockWebContext.create().setRequestMethod("POST"), VALUE);
        assertTrue(action instanceof OkAction);
        assertEquals(VALUE, ((OkAction) action).getContent());
    }

    @Test
    public void testFormPostContentAfterPostWithoutModernCode() {
        HttpActionHelper.setUseModernHttpCodes(false);
        final var action = HttpActionHelper
            .buildFormPostContentAction(MockWebContext.create().setRequestMethod("POST"), VALUE);
        assertTrue(action instanceof OkAction);
        assertEquals(VALUE, ((OkAction) action).getContent());
    }

    @Test
    public void testBuildFormPostContent() {
        final var content = HttpActionHelper.buildFormPostContent(MockWebContext.create().setFullRequestURL(CALLBACK_URL));
        assertEquals("<html>\n<body>\n<form action=\"" + CALLBACK_URL + "\" name=\"f\" method=\"post\">\n"
            + "<input value='POST' type='submit' />\n</form>\n" +
            "<script type='text/javascript'>document.forms['f'].submit();</script>\n" +
            "</body>\n</html>\n", content);
    }

    @Test
    public void testBuildFormPostContentWithData() {
        final var content = HttpActionHelper
            .buildFormPostContent(MockWebContext.create().setFullRequestURL(CALLBACK_URL).addRequestParameter(NAME, VALUE));
        assertEquals("<html>\n<body>\n<form action=\"" + CALLBACK_URL + "\" name=\"f\" method=\"post\">\n"
            + "<input type='hidden' name=\"" + NAME + "\" value=\"" + VALUE + "\" />\n" +
            "<input value='POST' type='submit' />\n</form>\n" +
            "<script type='text/javascript'>document.forms['f'].submit();</script>\n" +
            "</body>\n</html>\n", content);
    }

    @Test
    public void testBuildUnauthenticated401WithHeader() {
        final WebContext context = MockWebContext.create();
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, VALUE);
        final var action = HttpActionHelper.buildUnauthenticatedAction(context);
        assertEquals(UnauthorizedAction.INSTANCE, action);
        assertEquals(VALUE, context.getResponseHeader(HttpConstants.AUTHENTICATE_HEADER).get());
    }

    @Test
    public void testBuildUnauthenticated401WithoutHeader() {
        final WebContext context = MockWebContext.create();
        final var action = HttpActionHelper.buildUnauthenticatedAction(context);
        assertEquals(UnauthorizedAction.INSTANCE, action);
        assertEquals("Bearer realm=\"pac4j\"", context.getResponseHeader(HttpConstants.AUTHENTICATE_HEADER).get());
    }

    @Test
    public void testBuildUnauthenticated403WithHeader() {
        HttpActionHelper.setAlwaysUse401ForUnauthenticated(false);
        final WebContext context = MockWebContext.create();
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, VALUE);
        final var action = HttpActionHelper.buildUnauthenticatedAction(context);
        assertEquals(UnauthorizedAction.INSTANCE, action);
        assertEquals(VALUE, context.getResponseHeader(HttpConstants.AUTHENTICATE_HEADER).get());
    }

    @Test
    public void testBuildUnauthenticated403WithoutHeader() {
        HttpActionHelper.setAlwaysUse401ForUnauthenticated(false);
        final WebContext context = MockWebContext.create();
        final var action = HttpActionHelper.buildUnauthenticatedAction(context);
        assertEquals(ForbiddenAction.INSTANCE, action);
        assertTrue(context.getResponseHeader(HttpConstants.AUTHENTICATE_HEADER).isEmpty());
    }
}
