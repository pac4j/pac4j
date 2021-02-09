package org.pac4j.core.engine.savedrequest;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.http.*;
import org.pac4j.core.util.HttpActionHelper;
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
        HttpActionHelper.setUseModernHttpCodes(true);
    }

    @Test
    public void testSaveGet() {
        final var context = MockWebContext.create().setFullRequestURL(PAC4J_URL);
        final var sessionStore = new MockSessionStore();
        handler.save(context, sessionStore);
        final var location = (String) sessionStore.get(context, Pac4jConstants.REQUESTED_URL).get();
        assertEquals(PAC4J_URL, location);
    }

    @Test
    public void testSavePost() {
        final var context = MockWebContext.create().setFullRequestURL(PAC4J_URL).setRequestMethod("POST");
        context.addRequestParameter(KEY, VALUE);
        final var sessionStore = new MockSessionStore();
        handler.save(context, sessionStore);
        final var action = (OkAction) sessionStore.get(context, Pac4jConstants.REQUESTED_URL).get();
        assertEquals(FORM_DATA, action.getContent());
    }

    @Test
    public void testRestoreNoRequestedUrl() {
        final var context = MockWebContext.create();
        final var sessionStore = new MockSessionStore();
        final var action = handler.restore(context, sessionStore, LOGIN_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(LOGIN_URL, ((FoundAction) action).getLocation());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreEmptyString() {
        final var context = MockWebContext.create();
        final var sessionStore = new MockSessionStore();
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, null);
        final var action = handler.restore(context, sessionStore, LOGIN_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(LOGIN_URL, ((FoundAction) action).getLocation());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreURLString() {
        final var context = MockWebContext.create();
        final var sessionStore = new MockSessionStore();
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, VALUE);
        final var action = handler.restore(context, sessionStore, LOGIN_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(VALUE, ((FoundAction) action).getLocation());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreFoundAction() {
        final var context = MockWebContext.create();
        final var sessionStore = new MockSessionStore();
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, new FoundAction(PAC4J_URL));
        final var action = handler.restore(context, sessionStore, LOGIN_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(PAC4J_URL, ((FoundAction) action).getLocation());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreFoundActionAfterPost() {
        final var context = MockWebContext.create();
        context.setRequestMethod("POST");
        final var sessionStore = new MockSessionStore();
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, new FoundAction(PAC4J_URL));
        final var action = handler.restore(context, sessionStore, LOGIN_URL);
        assertTrue(action instanceof SeeOtherAction);
        assertEquals(PAC4J_URL, ((SeeOtherAction) action).getLocation());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreOkAction() {
        final var context = MockWebContext.create().setFullRequestURL(PAC4J_URL).addRequestParameter(KEY, VALUE);
        final var formPost = HttpActionHelper.buildFormPostContent(context);
        final var sessionStore = new MockSessionStore();
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, new OkAction(formPost));
        final var action = handler.restore(context, sessionStore, LOGIN_URL);
        assertTrue(action instanceof OkAction);
        assertEquals(FORM_DATA, ((OkAction) action).getContent());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreOkActionAfterPost() {
        final var context = MockWebContext.create().setFullRequestURL(PAC4J_URL).addRequestParameter(KEY, VALUE);
        final var formPost = HttpActionHelper.buildFormPostContent(context);
        context.setRequestMethod("POST");
        final var sessionStore = new MockSessionStore();
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, new OkAction(formPost));
        final var action = handler.restore(context, sessionStore, LOGIN_URL);
        assertTrue(action instanceof OkAction);
        assertEquals(FORM_DATA, ((OkAction) action).getContent());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }
}
