package org.pac4j.oidc.credentials;

import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oidc.profile.OidcProfileTests;

import java.text.ParseException;

import static org.junit.Assert.*;

/**
 * Tests {@link OidcCredentials}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class OidcCredentialsTests implements TestsConstants {

    @Test
    public void testSerialization() throws ParseException {
        final OidcCredentials credentials = new OidcCredentials(new AuthorizationCode(VALUE), CLIENT_NAME);
        credentials.setAccessToken(new BearerAccessToken(VALUE, 0L, Scope.parse("oidc email")));
        credentials.setRefreshToken(new RefreshToken(VALUE));
        credentials.setIdToken(JWTParser.parse(OidcProfileTests.ID_TOKEN));
        byte[] result = SerializationUtils.serialize(credentials);
        final OidcCredentials credentials2 = SerializationUtils.deserialize(result);
        assertEquals(credentials.getAccessToken(), credentials2.getAccessToken());
        assertEquals(credentials.getRefreshToken(), credentials2.getRefreshToken());
        assertEquals(credentials.getIdToken().getParsedString(), credentials2.getIdToken().getParsedString());
    }
}
