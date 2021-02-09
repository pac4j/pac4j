package org.pac4j.http.client.indirect;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.core.credentials.UsernamePasswordCredentials;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * This class tests the {@link FormClient} class.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class FormClientTests implements TestsConstants {

    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        final var formClient = new FormClient(LOGIN_URL, null);
        formClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.expectException(() -> formClient.getCredentials(MockWebContext.create(), new MockSessionStore()),
            TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final var formClient = new FormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator());
        formClient.setCallbackUrl(CALLBACK_URL);
        formClient.setProfileCreator(null);
        TestsHelper.expectException(() -> formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, PASSWORD),
                MockWebContext.create(), new MockSessionStore()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final var formClient = new FormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator());
        formClient.setCallbackUrl(CALLBACK_URL);
        formClient.init();
    }

    @Test
    public void testMissingLoginUrl() {
        final var formClient = new FormClient(null, new SimpleTestUsernamePasswordAuthenticator());
        formClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(formClient, "loginUrl cannot be blank");
    }

    private FormClient getFormClient() {
        final var client = new FormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator());
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    public void testRedirectionUrl() {
        final var formClient = getFormClient();
        var context = MockWebContext.create();
        final var action = (FoundAction) formClient.getRedirectionAction(context, new MockSessionStore()).get();
        assertEquals(LOGIN_URL, action.getLocation());
    }

    @Test
    public void testGetCredentialsMissingUsername() {
        final var formClient = getFormClient();
        final var context = MockWebContext.create();
        final var action = (FoundAction) TestsHelper.expectException(
            () -> formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME),
                new MockSessionStore()));
        assertEquals(302, action.getCode());
        assertEquals(LOGIN_URL + "?" + formClient.getUsernameParameter() + "=" + USERNAME + "&"
                + FormClient.ERROR_PARAMETER + "=" + FormClient.MISSING_FIELD_ERROR, action.getLocation());
    }

    @Test
    public void testGetCredentialsMissingPassword() {
        final var formClient = getFormClient();
        final var context = MockWebContext.create();
        final var action = (FoundAction) TestsHelper.expectException(
            () -> formClient.getCredentials(context.addRequestParameter(formClient.getPasswordParameter(), PASSWORD),
                new MockSessionStore()));
        assertEquals(302, action.getCode());
        assertEquals(LOGIN_URL + "?" + formClient.getUsernameParameter() + "=&" + FormClient.ERROR_PARAMETER + "="
               + FormClient.MISSING_FIELD_ERROR, action.getLocation());
    }

    @Test
    public void testGetCredentials() {
        final var formClient = getFormClient();
        final var context = MockWebContext.create();
        final var action = (FoundAction) TestsHelper.expectException(
            () -> formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), PASSWORD), new MockSessionStore()));
        assertEquals(302, action.getCode());
        assertEquals(LOGIN_URL + "?" + formClient.getUsernameParameter() + "=" + USERNAME + "&"
                + FormClient.ERROR_PARAMETER + "=" + CredentialsException.class.getSimpleName(), action.getLocation());
    }

    @Test
    public void testGetRightCredentials() {
        final var formClient = getFormClient();
        final var credentials = (UsernamePasswordCredentials) formClient.getCredentials(MockWebContext.create()
                .addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), USERNAME), new MockSessionStore()).get();
        assertEquals(USERNAME, credentials.getUsername());
        assertEquals(USERNAME, credentials.getPassword());
    }

    @Test
    public void testGetUserProfile() {
        final var formClient = getFormClient();
        formClient.setProfileCreator((credentials, context, session) -> {
            var username = ((UsernamePasswordCredentials) credentials).getUsername();
            final var profile = new CommonProfile();
            profile.setId(username);
            profile.addAttribute(Pac4jConstants.USERNAME, username);
            return Optional.of(profile);
        });
        final var context = MockWebContext.create();
        final var profile = (CommonProfile) formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, USERNAME),
            context, new MockSessionStore()).get();
        assertEquals(USERNAME, profile.getId());
        assertEquals(CommonProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + USERNAME, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), CommonProfile.class));
        assertEquals(USERNAME, profile.getUsername());
        assertEquals(1, profile.getAttributes().size());
    }
}
