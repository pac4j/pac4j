package org.pac4j.oauth.credentials;

import com.github.scribejava.core.model.OAuth1RequestToken;
import org.junit.Test;
import org.pac4j.core.util.serializer.JavaSerializer;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the {@link OAuth10Credentials} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuth10CredentialsTests implements TestsConstants {

    private final static OAuth1RequestToken REQUEST_TOKEN = new OAuth1RequestToken(TOKEN, SECRET);

    @Test
    public void testOAuth10Credentials() {
        final var credentials = new OAuth10Credentials(REQUEST_TOKEN, TOKEN, VERIFIER);
        assertEquals(TOKEN, credentials.getToken());
        assertEquals(VERIFIER, credentials.getVerifier());
        final var requestToken = credentials.getRequestToken();
        assertEquals(TOKEN, requestToken.getToken());
        assertEquals(SECRET, requestToken.getTokenSecret());
        // test serialization
        final var javaSerializer = new JavaSerializer();
        final var bytes = javaSerializer.serializeToBytes(credentials);
        final var credentials2 = (OAuth10Credentials) javaSerializer.deserializeFromBytes(bytes);
        assertEquals(credentials.getRequestToken().toString(), credentials2.getRequestToken().toString());
        assertEquals(credentials.getToken(), credentials2.getToken());
        assertEquals(credentials.getVerifier(), credentials2.getVerifier());
    }
}
