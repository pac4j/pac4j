package org.pac4j.http.client.direct;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.credentials.CredentialUtil;
import org.pac4j.http.credentials.DigestCredentials;
import org.pac4j.http.credentials.authenticator.test.SimpleTestDigestAuthenticator;
import org.pac4j.http.credentials.authenticator.test.SimpleTestTokenAuthenticator;

import static org.junit.Assert.assertEquals;
import static org.pac4j.core.context.HttpConstants.*;

/**
 * This class tests the {@link DirectDigestAuthClient} class.
 *
 * @author Mircea Carasel
 * @since 1.9.0
 */
public class DirectDigestAuthClientTests implements TestsConstants {

    @Test
    public void testMissingUsernamePasswordAuthenticator() {
        final DirectDigestAuthClient digestAuthClient = new DirectDigestAuthClient(null);
        TestsHelper.expectException(() -> digestAuthClient.getCredentials(MockWebContext.create()), TechnicalException.class,
            "authenticator cannot be null");
    }

    @Test
    public void testMissingProfileCreator() {
        final DirectDigestAuthClient digestAuthClient = new DirectDigestAuthClient(new SimpleTestTokenAuthenticator(), null);
        TestsHelper.expectException(() -> digestAuthClient.getUserProfile(new DigestCredentials(TOKEN, HTTP_METHOD.POST.name(),
                null, null, null, null, null, null, null), MockWebContext.create()), TechnicalException.class,
                "profileCreator cannot be null");
    }

    @Test
    public void testHasDefaultProfileCreator() {
        final DirectDigestAuthClient digestAuthClient = new DirectDigestAuthClient(new SimpleTestTokenAuthenticator());
        digestAuthClient.init();
    }

    @Test
    public void testAuthentication() {
        final DirectDigestAuthClient client = new DirectDigestAuthClient(new SimpleTestDigestAuthenticator());
        client.setRealm(REALM);
        final MockWebContext context = MockWebContext.create();
        context.addRequestHeader(AUTHORIZATION_HEADER,
                DIGEST_AUTHORIZATION_HEADER_VALUE);
        context.setRequestMethod(HTTP_METHOD.GET.name());

        final DigestCredentials credentials = client.getCredentials(context).get();

        final CommonProfile profile = (CommonProfile) client.getUserProfile(credentials, context).get();

        String ha1 = CredentialUtil.encryptMD5(USERNAME + ":" + REALM + ":" +PASSWORD);
        String serverDigest1 = credentials.calculateServerDigest(true, ha1);
        String serverDigest2 = credentials.calculateServerDigest(false, PASSWORD);
        assertEquals(DIGEST_RESPONSE, serverDigest1);
        assertEquals(DIGEST_RESPONSE, serverDigest2);
        assertEquals(USERNAME, profile.getId());
    }
}
