package org.pac4j.oauth.credentials;

import com.github.scribejava.core.model.OAuth1RequestToken;
import org.junit.Test;
import org.pac4j.core.util.JavaSerializationHelper;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the {@link OAuthCredentials} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuthCredentialsTests implements TestsConstants {

    private final static OAuth1RequestToken REQUEST_TOKEN = new OAuth1RequestToken(TOKEN, SECRET);

    @Test
    public void testOAuth10Credentials() {
        final OAuth10Credentials credentials = new OAuth10Credentials(REQUEST_TOKEN, TOKEN, VERIFIER);
        assertEquals(TOKEN, credentials.getToken());
        assertEquals(VERIFIER, credentials.getVerifier());
        final OAuth1RequestToken requestToken = credentials.getRequestToken();
        assertEquals(TOKEN, requestToken.getToken());
        assertEquals(SECRET, requestToken.getTokenSecret());
        // test serialization
        final JavaSerializationHelper javaSerializationHelper = new JavaSerializationHelper();
        final byte[] bytes = javaSerializationHelper.serializeToBytes(credentials);
        final OAuth10Credentials credentials2 = (OAuth10Credentials) javaSerializationHelper.deserializeFromBytes(bytes);
        assertEquals(credentials.getRequestToken().toString(), credentials2.getRequestToken().toString());
        assertEquals(credentials.getToken(), credentials2.getToken());
        assertEquals(credentials.getVerifier(), credentials2.getVerifier());
    }
}
