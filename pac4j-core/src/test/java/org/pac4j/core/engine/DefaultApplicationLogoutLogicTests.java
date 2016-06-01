package org.pac4j.core.engine;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.HttpActionAdapter;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link DefaultApplicationLogoutLogic}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class DefaultApplicationLogoutLogicTests implements TestsConstants {

    private ApplicationLogoutLogic<Object, WebContext> logic;

    private MockWebContext context;

    private Config config;

    private HttpActionAdapter<Object, WebContext> httpActionAdapter;

    private String defaultUrl;

    private String logoutUrlPattern;

    @Before
    public void setUp() {
        logic = new DefaultApplicationLogoutLogic<>();
        context = MockWebContext.create();
        config = new Config();
        httpActionAdapter = (code, ctx) -> null;
        defaultUrl = null;
        logoutUrlPattern = null;
    }

    private void call() {
        logic.perform(context, config, httpActionAdapter, defaultUrl, logoutUrlPattern);
    }

    @Test
    public void testNullConfig() {
        config = null;
        TestsHelper.expectException(() -> call(), TechnicalException.class, "config cannot be null");
    }

    @Test
    public void testNullContext() {
        context = null;
        TestsHelper.expectException(() -> call(), TechnicalException.class, "context cannot be null");
    }

    @Test
    public void testNullHttpActionAdapter() {
        httpActionAdapter = null;
        TestsHelper.expectException(() -> call(), TechnicalException.class, "httpActionAdapter cannot be null");
    }

    @Test
    public void testBlankLogoutUrlPattern() {
        logoutUrlPattern = "";
        TestsHelper.expectException(() -> call(), TechnicalException.class, "logoutUrlPattern cannot be blank");
    }

    @Test
    public void testLogout() {
        final LinkedHashMap<String, CommonProfile> profiles = new LinkedHashMap<>();
        profiles.put(NAME, new CommonProfile());
        context.setRequestAttribute(Pac4jConstants.USER_PROFILES, profiles);
        context.setSessionAttribute(Pac4jConstants.USER_PROFILES, profiles);
        call();
        assertEquals(200, context.getResponseStatus());
        assertEquals("", context.getResponseContent());
        final LinkedHashMap<String, CommonProfile> profiles2 = (LinkedHashMap<String, CommonProfile>) context.getRequestAttribute(Pac4jConstants.USER_PROFILES);
        assertEquals(0, profiles2.size());
        final LinkedHashMap<String, CommonProfile> profiles3 = (LinkedHashMap<String, CommonProfile>) context.getSessionAttribute(Pac4jConstants.USER_PROFILES);
        assertEquals(0, profiles3.size());
    }

    @Test
    public void testLogoutWithDefaultUrl() {
        defaultUrl = CALLBACK_URL;
        call();
        assertEquals(302, context.getResponseStatus());
        assertEquals(CALLBACK_URL, context.getResponseLocation());
    }

    @Test
    public void testLogoutWithGoodUrl() {
        context.addRequestParameter(Pac4jConstants.URL, PATH);
        call();
        assertEquals(302, context.getResponseStatus());
        assertEquals(PATH, context.getResponseLocation());
    }

    @Test
    public void testLogoutWithBadUrlNoDefaultUrl() {
        context.addRequestParameter(Pac4jConstants.URL, PATH);
        logoutUrlPattern = VALUE;
        call();
        assertEquals(200, context.getResponseStatus());
        assertEquals("", context.getResponseContent());
    }

    @Test
    public void testLogoutWithBadUrlButDefaultUrl() {
        context.addRequestParameter(Pac4jConstants.URL, PATH);
        defaultUrl = CALLBACK_URL;
        logoutUrlPattern = VALUE;
        call();
        assertEquals(302, context.getResponseStatus());
        assertEquals(CALLBACK_URL, context.getResponseLocation());
    }
}
