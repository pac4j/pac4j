package org.pac4j.jwt;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import org.joda.time.DateTime;
import org.junit.Test;
import org.pac4j.cas.profile.CasRestProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.jwt.config.encryption.SecretEncryptionConfiguration;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.profile.JwtGenerator;

import static org.junit.Assert.*;

/**
 * Tests the CasRestProfile in JWT generation/authentication.
 *
 * @author Jerome LELEU
 * @since 3.7.0
 */
public class CasRestProfileJwtTest implements TestsConstants {

    private static final String TGT_ID = "TGT-123";

    @Test
    public void testGenerateAuthenticate() {
        final var signingSecret = CommonHelper.randomString(32);
        final var encryptionSecret = CommonHelper.randomString(32);
        final var generator = new JwtGenerator(new SecretSignatureConfiguration(signingSecret, JWSAlgorithm.HS256),
            new SecretEncryptionConfiguration(encryptionSecret, JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256));
        final var casRestProfile = new CasRestProfile(TGT_ID, USERNAME);
        assertNotNull(casRestProfile.getTicketGrantingTicketId());

        var token = generator.generate(casRestProfile);
        final var jwtAuthenticator = new JwtAuthenticator(new SecretSignatureConfiguration(signingSecret, JWSAlgorithm.HS256),
            new SecretEncryptionConfiguration(encryptionSecret, JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256));
        jwtAuthenticator.setExpirationTime(DateTime.now().plusMinutes(5).toDate());
        final var newCasProfile = (CasRestProfile) jwtAuthenticator.validateToken(token);
        assertEquals(TGT_ID, newCasProfile.getTicketGrantingTicketId());
    }
}
