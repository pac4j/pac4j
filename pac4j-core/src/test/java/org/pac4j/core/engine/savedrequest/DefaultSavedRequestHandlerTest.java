package org.pac4j.core.engine.savedrequest;

import lombok.val;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.exception.http.SeeOtherAction;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.Pac4jConstants;
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
        val context = MockWebContext.create().setFullRequestURL(PAC4J_URL);
        val sessionStore = new MockSessionStore();
        handler.save(new CallContext(context, sessionStore));
        val location = (String) sessionStore.get(context, Pac4jConstants.REQUESTED_URL).get();
        assertEquals(PAC4J_URL, location);
    }

    @Test
    public void testSavePost() {
        val context = MockWebContext.create().setFullRequestURL(PAC4J_URL).setRequestMethod("POST");
        context.addRequestParameter(KEY, VALUE);
        val sessionStore = new MockSessionStore();
        handler.save(new CallContext(context, sessionStore));
        val action = (OkAction) sessionStore.get(context, Pac4jConstants.REQUESTED_URL).get();
        assertEquals(FORM_DATA, action.getContent());
    }

    @Test
    public void testRestoreNoRequestedUrl() {
        val context = MockWebContext.create();
        val sessionStore = new MockSessionStore();
        val action = handler.restore(new CallContext(context, sessionStore), LOGIN_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(LOGIN_URL, ((FoundAction) action).getLocation());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreEmptyString() {
        val context = MockWebContext.create();
        val sessionStore = new MockSessionStore();
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, null);
        val action = handler.restore(new CallContext(context, sessionStore), LOGIN_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(LOGIN_URL, ((FoundAction) action).getLocation());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreURLString() {
        val context = MockWebContext.create();
        val sessionStore = new MockSessionStore();
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, VALUE);
        val action = handler.restore(new CallContext(context, sessionStore), LOGIN_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(VALUE, ((FoundAction) action).getLocation());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreFoundAction() {
        val context = MockWebContext.create();
        val sessionStore = new MockSessionStore();
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, new FoundAction(PAC4J_URL));
        val action = handler.restore(new CallContext(context, sessionStore), LOGIN_URL);
        assertTrue(action instanceof FoundAction);
        assertEquals(PAC4J_URL, ((FoundAction) action).getLocation());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreFoundActionAfterPost() {
        val context = MockWebContext.create();
        context.setRequestMethod("POST");
        val sessionStore = new MockSessionStore();
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, new FoundAction(PAC4J_URL));
        val action = handler.restore(new CallContext(context, sessionStore), LOGIN_URL);
        assertTrue(action instanceof SeeOtherAction);
        assertEquals(PAC4J_URL, ((SeeOtherAction) action).getLocation());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreOkAction() {
        val context = MockWebContext.create().setFullRequestURL(PAC4J_URL).addRequestParameter(KEY, VALUE);
        val formPost = HttpActionHelper.buildFormPostContent(context);
        val sessionStore = new MockSessionStore();
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, new OkAction(formPost));
        val action = handler.restore(new CallContext(context, sessionStore), LOGIN_URL);
        assertTrue(action instanceof OkAction);
        assertEquals(FORM_DATA, ((OkAction) action).getContent());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }

    @Test
    public void testRestoreOkActionAfterPost() {
        val context = MockWebContext.create().setFullRequestURL(PAC4J_URL).addRequestParameter(KEY, VALUE);
        val formPost = HttpActionHelper.buildFormPostContent(context);
        context.setRequestMethod("POST");
        val sessionStore = new MockSessionStore();
        sessionStore.set(context, Pac4jConstants.REQUESTED_URL, new OkAction(formPost));
        val action = handler.restore(new CallContext(context, sessionStore), LOGIN_URL);
        assertTrue(action instanceof OkAction);
        assertEquals(FORM_DATA, ((OkAction) action).getContent());
        assertFalse(sessionStore.get(context, Pac4jConstants.REQUESTED_URL).isPresent());
    }
}
