package org.pac4j.http.client.direct;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.CredentialUtil;
import org.pac4j.http.credentials.DigestCredentials;
import org.pac4j.http.credentials.authenticator.test.SimpleTestDigestAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;

import static org.junit.Assert.assertEquals;
import static org.pac4j.core.context.HttpConstants.AUTHORIZATION_HEADER;
import static org.pac4j.core.context.HttpConstants.HTTP_METHOD;

/**
 * This class tests the {@link DirectDigestAuthClient} class.
 *
 * @author Mircea Carasel
 * @since 1.9.0
 */
public class DirectDigestAuthClientTests implements TestsConstants {

    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        val digestAuthClient = new DirectDigestAuthClient(null);
        TestsHelper.expectException(() -> digestAuthClient.getCredentials(new CallContext(MockWebContext.create(),
                new MockSessionStore())), TechnicalException.class, "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        val digestAuthClient = new DirectDigestAuthClient(new SimpleTestTokenAuthenticator(), null);
        TestsHelper.expectException(() -> digestAuthClient.getUserProfile(new CallContext(MockWebContext.create(), new MockSessionStore()),
                new DigestCredentials(TOKEN, HTTP_METHOD.POST.name(), null, null, null, null, null, null, null)), TechnicalException.class,
                "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        val digestAuthClient = new DirectDigestAuthClient(new SimpleTestTokenAuthenticator());
        digestAuthClient.init();
    }

    @Test
    public void testAuthentication() {
        val client = new DirectDigestAuthClient(new SimpleTestDigestAuthenticator());
        client.setRealm(REALM);
        val context = MockWebContext.create();
        context.addRequestHeader(AUTHORIZATION_HEADER,
                DIGEST_AUTHORIZATION_HEADER_VALUE);
        context.setRequestMethod(HTTP_METHOD.GET.name());

        val credentials = (DigestCredentials) client.getCredentials(new CallContext(context, new MockSessionStore())).get();

        val profile = (CommonProfile) client.getUserProfile(new CallContext(context, new MockSessionStore()), credentials).get();

        val ha1 = CredentialUtil.encryptMD5(USERNAME + ":" + REALM + ":" +PASSWORD);
        val serverDigest1 = credentials.calculateServerDigest(true, ha1);
        val serverDigest2 = credentials.calculateServerDigest(false, PASSWORD);
        assertEquals(DIGEST_RESPONSE, serverDigest1);
        assertEquals(DIGEST_RESPONSE, serverDigest2);
        assertEquals(USERNAME, profile.getId());
    }
}
