package org.pac4j.http.client.indirect;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.authenticator.test.SimpleTestUsernamePasswordAuthenticator;
import org.pac4j.core.credentials.UsernamePasswordCredentials;

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
        final FormClient formClient = new FormClient(LOGIN_URL, null);
        formClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.expectException(() -> formClient.getCredentials(MockWebContext.create()), TechnicalException.class,
            "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final FormClient formClient = new FormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator());
        formClient.setCallbackUrl(CALLBACK_URL);
        formClient.setProfileCreator(null);
        TestsHelper.expectException(() -> formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, PASSWORD),
                MockWebContext.create()), TechnicalException.class, "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final FormClient formClient = new FormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator());
        formClient.setCallbackUrl(CALLBACK_URL);
        formClient.init();
    }

    @Test
    public void testMissingLoginUrl() {
        final FormClient formClient = new FormClient(null, new SimpleTestUsernamePasswordAuthenticator());
        formClient.setCallbackUrl(CALLBACK_URL);
        TestsHelper.initShouldFail(formClient, "loginUrl cannot be blank");
    }

    private FormClient getFormClient() {
        final FormClient client = new FormClient(LOGIN_URL, new SimpleTestUsernamePasswordAuthenticator());
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    public void testRedirectionUrl() {
        final FormClient formClient = getFormClient();
        MockWebContext context = MockWebContext.create();
        final FoundAction action = (FoundAction) formClient.redirect(context).get();
        assertEquals(LOGIN_URL, action.getLocation());
    }

    @Test
    public void testGetCredentialsMissingUsername() {
        final FormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        final FoundAction action = (FoundAction) TestsHelper.expectException(
            () -> formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME)));
        assertEquals(302, action.getCode());
        assertEquals(LOGIN_URL + "?" + formClient.getUsernameParameter() + "=" + USERNAME + "&"
                + FormClient.ERROR_PARAMETER + "=" + FormClient.MISSING_FIELD_ERROR, action.getLocation());
    }

    @Test
    public void testGetCredentialsMissingPassword() {
        final FormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        final FoundAction action = (FoundAction) TestsHelper.expectException(
            () -> formClient.getCredentials(context.addRequestParameter(formClient.getPasswordParameter(), PASSWORD)));
        assertEquals(302, action.getCode());
        assertEquals(LOGIN_URL + "?" + formClient.getUsernameParameter() + "=&" + FormClient.ERROR_PARAMETER + "="
               + FormClient.MISSING_FIELD_ERROR, action.getLocation());
    }

    @Test
    public void testGetCredentials() {
        final FormClient formClient = getFormClient();
        final MockWebContext context = MockWebContext.create();
        final FoundAction action = (FoundAction) TestsHelper.expectException(
            () -> formClient.getCredentials(context.addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), PASSWORD)));
        assertEquals(302, action.getCode());
        assertEquals(LOGIN_URL + "?" + formClient.getUsernameParameter() + "=" + USERNAME + "&"
                + FormClient.ERROR_PARAMETER + "=" + CredentialsException.class.getSimpleName(), action.getLocation());
    }

    @Test
    public void testGetRightCredentials() {
        final FormClient formClient = getFormClient();
        final UsernamePasswordCredentials credentials = formClient.getCredentials(MockWebContext.create()
                .addRequestParameter(formClient.getUsernameParameter(), USERNAME)
                .addRequestParameter(formClient.getPasswordParameter(), USERNAME)).get();
        assertEquals(USERNAME, credentials.getUsername());
        assertEquals(USERNAME, credentials.getPassword());
    }

    @Test
    public void testGetUserProfile() {
        final FormClient formClient = getFormClient();
        formClient.setProfileCreator((credentials, context) -> {
            String username = credentials.getUsername();
            final CommonProfile profile = new CommonProfile();
            profile.setId(username);
            profile.addAttribute(Pac4jConstants.USERNAME, username);
            return profile;
        });
        final MockWebContext context = MockWebContext.create();
        final CommonProfile profile =
            (CommonProfile) formClient.getUserProfile(new UsernamePasswordCredentials(USERNAME, USERNAME), context);
        assertEquals(USERNAME, profile.getId());
        assertEquals(CommonProfile.class.getName() + CommonProfile.SEPARATOR + USERNAME, profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), CommonProfile.class));
        assertEquals(USERNAME, profile.getUsername());
        assertEquals(1, profile.getAttributes().size());
    }
}
