package org.pac4j.oauth.credentials;

import com.github.scribejava.core.model.OAuth2AccessToken;
import lombok.val;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.serializer.JavaSerializer;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the {@link OAuth20Credentials} class.
 *
 * @author Misagh Moayyed
 * @since 6.0.5
 */
public final class OAuth20CredentialsTests implements TestsConstants {

    @Test
    public void testOAuth20Credentials() {
        val realAccessToken = new OAuth2AccessToken(UUID.randomUUID().toString(), "{'access_token': 'abc'}");
        val credentials = new OAuth20Credentials(UUID.randomUUID().toString());
        credentials.setAccessToken(OAuth20Credentials.OAuth20AccessToken.from(realAccessToken));
        val javaSerializer = new JavaSerializer();
        val bytes = javaSerializer.serializeToBytes(credentials);
        val credentials2 = (OAuth20Credentials) javaSerializer.deserializeFromBytes(bytes);
        assertEquals(credentials2, credentials);
    }
}
