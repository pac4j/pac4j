package org.pac4j.oauth.credentials;

import com.github.scribejava.core.model.Token;
import org.junit.Test;
import org.pac4j.core.util.JavaSerializationHelper;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * This class tests the {@link OAuthCredentials} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OAuthCredentialsTests implements TestsConstants {

    private final static Token REQUEST_TOKEN = new Token(TOKEN, SECRET);

    @Test
    public void testOAuthCredentials() {
        final OAuthCredentials credentials = new OAuthCredentials(REQUEST_TOKEN, TOKEN, VERIFIER, TYPE);
        assertEquals(TOKEN, credentials.getToken());
        assertEquals(VERIFIER, credentials.getVerifier());
        assertEquals(TYPE, credentials.getClientName());
        final Token requestToken = credentials.getRequestToken();
        assertEquals(TOKEN, requestToken.getToken());
        assertEquals(SECRET, requestToken.getSecret());
        // test serialization
        final JavaSerializationHelper javaSerializationHelper = new JavaSerializationHelper();
        final byte[] bytes = javaSerializationHelper.serializeToBytes(credentials);
        final OAuthCredentials credentials2 = (OAuthCredentials) javaSerializationHelper.unserializeFromBytes(bytes);
        assertEquals(credentials.getRequestToken().toString(), credentials2.getRequestToken().toString());
        assertEquals(credentials.getToken(), credentials2.getToken());
        assertEquals(credentials.getVerifier(), credentials2.getVerifier());
        assertEquals(credentials.getClientName(), credentials2.getClientName());
    }
}
