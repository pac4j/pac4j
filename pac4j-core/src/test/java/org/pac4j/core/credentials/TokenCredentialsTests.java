package org.pac4j.core.credentials;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.serializer.JsonSerializer;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * Tests {@link TokenCredentials}.
 *
 * @author Hayden Sartoris
 * @since 6.0.0
 */

public final class TokenCredentialsTests implements TestsConstants {

    @Test
    public void testJsonSerialization() throws Exception {
        val tokenCredentials = new TokenCredentials("token");
        val profile = new CommonProfile();
        profile.setId("id");
        tokenCredentials.setUserProfile(profile);

        val jsonSerializer = new JsonSerializer(TokenCredentials.class);
        val json = jsonSerializer.serializeToString(tokenCredentials);
        val result = jsonSerializer.deserializeFromString(json);
        assertEquals(tokenCredentials, result);

    }
}
