package org.pac4j.jwt.config.signature;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.test.util.TestsHelper;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks a signature configuration written before {@code buildSigner} existed keeps working: it implements
 * {@code sign(JWTClaimsSet)} itself and must neither fail to compile nor change behaviour.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class LegacySignatureConfigurationTests {

    private static final String SECRET = "12345678901234567890123456789012";

    /** A configuration as it would have been written before the header aware signature was added. */
    private static class LegacySignatureConfiguration extends AbstractSignatureConfiguration {

        LegacySignatureConfiguration() {
            algorithm = JWSAlgorithm.HS256;
        }

        @Override
        protected void internalInit(final boolean forceReinit) {
        }

        @Override
        public boolean supports(final JWSAlgorithm algorithm) {
            return JWSAlgorithm.HS256.equals(algorithm);
        }

        @Override
        public SignedJWT sign(final JWTClaimsSet claims) {
            try {
                val signedJWT = new SignedJWT(new JWSHeader(algorithm), claims);
                signedJWT.sign(new MACSigner(SECRET));
                return signedJWT;
            } catch (final JOSEException e) {
                throw new TechnicalException(e);
            }
        }

        @Override
        public boolean verify(final SignedJWT jwt) throws JOSEException {
            return jwt.verify(new MACVerifier(SECRET));
        }
    }

    @Test
    void testTheLegacySignatureStillWorks() throws Exception {
        val configuration = new LegacySignatureConfiguration();
        val signedJWT = configuration.sign(new JWTClaimsSet.Builder().subject("jle").build());

        assertTrue(configuration.verify(signedJWT));
    }

    @Test
    void testOnlyTheHeaderAwareSignatureIsMissing() {
        val configuration = new LegacySignatureConfiguration();

        TestsHelper.expectException(
            () -> configuration.sign(new JWSHeader(JWSAlgorithm.HS256), new JWTClaimsSet.Builder().build()),
            TechnicalException.class,
            "no signer built by " + LegacySignatureConfiguration.class.getName()
                + ": override buildSigner to sign with a specific header");
    }
}
