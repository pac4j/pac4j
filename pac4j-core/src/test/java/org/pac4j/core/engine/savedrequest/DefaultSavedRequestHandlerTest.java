package org.pac4j.core.engine.savedrequest;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.http.*;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * Tests {@link DefaultSavedRequestHandler}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public class DefaultSavedRequestHandlerTest implements TestsConstants {

    private static final String FORM_DATA = "<html>\n" +
        "<body>\n" +
        "<form action=\"http://www.pac4j.org/test.html\" name=\"f\" method=\"post\">\n" +
        "<input type='hidden' name=\"key\" value=\"value\" />\n" +
        "<input value='POST' type='submit' />\n" +
        "</form>\n" +
        "<script type='text/javascript'>document.forms['f'].submit();</script>\n" +
        "</body>\n" +
        "</html>\n";

    private DefaultSavedRequestHandler handler = new DefaultSavedRequestHandler();

    @BeforeClass
    public static void beforeClass() {
        RedirectionActionHelper.setUseModernHttpCodes(true);
    }

    @Test
    public void testSaveGet() {
        final MockWebContext context = MockWebContext.create().setFullRequestURL(PAC4J_URL);
        handler.save(context);
        final MockSessionStore sessionStore = (MockSessionStore) context.getSessionStore();
        final FoundAction action = (FoundAction) sessionStore.get(context, Pac4jConstants.REQUESTED_URL).get();
        assertEquals(PAC4J_URL, action.getLocation());
    }

    @Test
    public void testSavePost() {
        final MockWebContext context = MockWebContext.create().setFullRequestURL(PAC4J_URL).setRequestMethod("POST");
        context.addRequestParameter(KEY, VALUE);
        handler.save(context);
        final MockSessionStore sessionStore = (MockSessionStore) context.getSessionStore();
        final OkAction action = (OkAction) sessionStore.get(context, Pac4jConstants.REQUESTED_URL).get();
        assertEquals(FORM_DATA, action.getContent());
    }

    @Test
    public void testRestoreNoRequestedUrl() {
        final MockWebContext context = MockWebContext.create();
        final HttpAction action = handler.restore(context, LOGIN_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(LOGIN_URL, ((FoundAction) action).getLocation());
        assertFalse(context.getSessionStore().get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreEmptyString() {
        final MockWebContext context = MockWebContext.create();
        context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, "");
        final HttpAction action = handler.restore(context, LOGIN_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(LOGIN_URL, ((FoundAction) action).getLocation());
        assertEquals("", context.getSessionStore().get(context, Pac4jConstants.REQUESTED_URL).get());
    }

    @Test
    public void testRestoreStringURL() {
        final MockWebContext context = MockWebContext.create();
        context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, PAC4J_URL);
        final HttpAction action = handler.restore(context, LOGIN_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(PAC4J_URL, ((FoundAction) action).getLocation());
        assertEquals("", context.getSessionStore().get(context, Pac4jConstants.REQUESTED_URL).get());
    }

    @Test
    public void testRestoreFoundAction() {
        final MockWebContext context = MockWebContext.create();
        context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, new FoundAction(PAC4J_URL));
        final HttpAction action = handler.restore(context, LOGIN_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(PAC4J_URL, ((FoundAction) action).getLocation());
        assertEquals("", context.getSessionStore().get(context, Pac4jConstants.REQUESTED_URL).get());
    }

    @Test
    public void testRestoreFoundActionAfterPost() {
        final MockWebContext context = MockWebContext.create();
        context.setRequestMethod("POST");
        context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, new FoundAction(PAC4J_URL));
        final HttpAction action = handler.restore(context, LOGIN_URL);
        assertTrue(action instanceof SeeOtherAction);
        assertEquals(PAC4J_URL, ((SeeOtherAction) action).getLocation());
        assertEquals("", context.getSessionStore().get(context, Pac4jConstants.REQUESTED_URL).get());
    }

    @Test
    public void testRestoreOkAction() {
        final MockWebContext context = MockWebContext.create().setFullRequestURL(PAC4J_URL).addRequestParameter(KEY, VALUE);
        final String formPost = RedirectionActionHelper.buildFormPostContent(context);
        context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, new OkAction(formPost));
        final HttpAction action = handler.restore(context, LOGIN_URL);
        assertTrue(action instanceof OkAction);
        assertEquals(FORM_DATA, ((OkAction) action).getContent());
        assertEquals("", context.getSessionStore().get(context, Pac4jConstants.REQUESTED_URL).get());
    }

    @Test
    public void testRestoreOkActionAfterPost() {
        final MockWebContext context = MockWebContext.create().setFullRequestURL(PAC4J_URL).addRequestParameter(KEY, VALUE);
        final String formPost = RedirectionActionHelper.buildFormPostContent(context);
        context.setRequestMethod("POST");
        context.getSessionStore().set(context, Pac4jConstants.REQUESTED_URL, new OkAction(formPost));
        final HttpAction action = handler.restore(context, LOGIN_URL);
        assertTrue(action instanceof TemporaryRedirectAction);
        assertEquals(FORM_DATA, ((TemporaryRedirectAction) action).getContent());
        assertEquals("", context.getSessionStore().get(context, Pac4jConstants.REQUESTED_URL).get());
    }
}
