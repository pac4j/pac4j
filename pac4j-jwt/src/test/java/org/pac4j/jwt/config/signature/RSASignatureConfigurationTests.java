package org.pac4j.jwt.config.signature;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.val;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.jwt.config.AbstractKeyEncryptionConfigurationTests;
import org.pac4j.jwt.util.JWKHelper;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static org.junit.Assert.assertTrue;

/**
 * Tests {@link RSASignatureConfiguration}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class RSASignatureConfigurationTests extends AbstractKeyEncryptionConfigurationTests {

    private JWTClaimsSet buildClaims() {
        return new JWTClaimsSet.Builder().subject(VALUE).build();
    }

    @Override
    protected String getAlgorithm() {
        return "RSA";
    }

    @Test
    public void testMissingPrivateKey() {
        val config = new RSASignatureConfiguration();
        TestsHelper.expectException(() -> config.sign(buildClaims()), TechnicalException.class, "privateKey cannot be null");
    }

    @Test
    public void testMissingPublicKey() {
        val config = new RSASignatureConfiguration();
        config.setPrivateKey((RSAPrivateKey) buildKeyPair().getPrivate());
        val signedJWT = config.sign(buildClaims());
        TestsHelper.expectException(() -> config.verify(signedJWT), TechnicalException.class, "publicKey cannot be null");
    }

    @Test
    public void testMissingAlgorithm() {
        val config = new RSASignatureConfiguration(buildKeyPair(), null);
        TestsHelper.expectException(config::init, TechnicalException.class, "algorithm cannot be null");
    }

    @Test
    public void testBadAlgorithm() {
        val config = new RSASignatureConfiguration(buildKeyPair(), JWSAlgorithm.HS256);
        TestsHelper.expectException(config::init, TechnicalException.class,
            "Only the RS256, RS384, RS512, PS256, PS384 and PS512 algorithms are supported for RSA signature");
    }

    @Test
    public void buildFromJwk() {
        val json = new RSAKey.Builder((RSAPublicKey) buildKeyPair().getPublic()).build().toJSONString();
        JWKHelper.buildRSAKeyPairFromJwk(json);
    }

    @Test
    public void testSignVerify() throws JOSEException {
        val config = new RSASignatureConfiguration(buildKeyPair());
        val claims = new JWTClaimsSet.Builder().subject(VALUE).build();
        val signedJwt = config.sign(claims);
        assertTrue(config.verify(signedJwt));
    }
}
