package org.pac4j.jwt.config;

import com.nimbusds.jwt.*;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import java.text.ParseException;

import static org.junit.Assert.*;

/**
 * Tests {@link DirectEncryptionConfiguration}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class DirectEncryptionConfigurationTests implements TestsConstants {

    private JWTClaimsSet buildClaims() {
        return new JWTClaimsSet.Builder().subject(VALUE).build();
    }

    @Test
    public void testMissingSecret() {
        final DirectEncryptionConfiguration config = new DirectEncryptionConfiguration();
        TestsHelper.expectException(config::init, TechnicalException.class, "secret cannot be blank");
    }

    @Test
    public void testMissingMethod() {
        final DirectEncryptionConfiguration config = new DirectEncryptionConfiguration(SECRET, null);
        TestsHelper.expectException(config::init, TechnicalException.class, "method cannot be null");
    }

    @Test
    public void testEncryptDecrypt() throws ParseException {
        final MacSignatureConfiguration macConfig = new MacSignatureConfiguration(MAC_SECRET);
        final SignedJWT signedJWT = macConfig.sign(buildClaims());

        final DirectEncryptionConfiguration config = new DirectEncryptionConfiguration(MAC_SECRET);
        final String token = config.encrypt(signedJWT);
        final EncryptedJWT encryptedJwt = (EncryptedJWT) JWTParser.parse(token);
        final SignedJWT signedJWT2 = config.decrypt(encryptedJwt);
        assertEquals(VALUE, signedJWT2.getJWTClaimsSet().getSubject());
    }
}
