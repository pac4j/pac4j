package org.pac4j.oauth.credentials;

import com.github.scribejava.core.model.OAuth1RequestToken;
import lombok.val;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.serializer.JavaSerializer;

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
        val credentials = new OAuth10Credentials(REQUEST_TOKEN, TOKEN, VERIFIER);
        assertEquals(TOKEN, credentials.getToken());
        assertEquals(VERIFIER, credentials.getVerifier());
        val requestToken = credentials.getRequestToken();
        assertEquals(TOKEN, requestToken.getToken());
        assertEquals(SECRET, requestToken.getTokenSecret());
        // test serialization
        val javaSerializer = new JavaSerializer();
        val bytes = javaSerializer.serializeToBytes(credentials);
        val credentials2 = (OAuth10Credentials) javaSerializer.deserializeFromBytes(bytes);
        assertEquals(credentials.getRequestToken().toString(), credentials2.getRequestToken().toString());
        assertEquals(credentials.getToken(), credentials2.getToken());
        assertEquals(credentials.getVerifier(), credentials2.getVerifier());
    }
}
