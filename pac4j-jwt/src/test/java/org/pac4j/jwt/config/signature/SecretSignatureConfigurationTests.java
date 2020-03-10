package org.pac4j.jwt.config.signature;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.jwt.util.JWKHelper;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link SecretSignatureConfiguration}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class SecretSignatureConfigurationTests implements TestsConstants {

    @Test
    public void testMissingSecret() {
        final SecretSignatureConfiguration config = new SecretSignatureConfiguration();
        TestsHelper.expectException(config::init, TechnicalException.class, "secret cannot be null");
    }

    @Test
    public void testMissingAlgorithm() {
        final SecretSignatureConfiguration config = new SecretSignatureConfiguration(MAC_SECRET, null);
        TestsHelper.expectException(config::init, TechnicalException.class, "algorithm cannot be null");
    }

    @Test
    public void testBadAlgorithm() {
        final SecretSignatureConfiguration config = new SecretSignatureConfiguration(MAC_SECRET, JWSAlgorithm.ES256);
        TestsHelper.expectException(config::init, TechnicalException.class,
            "Only the HS256, HS384 and HS512 algorithms are supported for HMac signature");
    }

    @Test
    public void buildFromJwk() throws UnsupportedEncodingException {
        final String json = new OctetSequenceKey.Builder(MAC_SECRET.getBytes("UTF-8")).build().toJSONObject().toJSONString();
        JWKHelper.buildSecretFromJwk(json);
    }

    @Test
    public void testSignVerify() throws JOSEException {
        final SecretSignatureConfiguration config = new SecretSignatureConfiguration(MAC_SECRET);
        final JWTClaimsSet claims = new JWTClaimsSet.Builder().subject(VALUE).build();
        final SignedJWT signedJwt = config.sign(claims);
        assertTrue(config.verify(signedJwt));
    }

    @Test
    public void testGetSecretInitializedWithByteArray(){
        final byte[] rndBytes = new byte[32];
        new SecureRandom().nextBytes(rndBytes);
        final String secret = new String(rndBytes,UTF_8);
        assertEquals(new SecretSignatureConfiguration(rndBytes).getSecret(),secret);
    }

    @Test
    public void testSecretBase64(){
        final byte[] rndBytes = new byte[32];
        new SecureRandom().nextBytes(rndBytes);
        final SecretSignatureConfiguration secretSignatureConfiguration = new SecretSignatureConfiguration();
        final String base64Secret = Base64.encode(rndBytes).toString();
        secretSignatureConfiguration.setSecretBase64(base64Secret);
        assertEquals(base64Secret,secretSignatureConfiguration.getSecretBase64());
    }

    @Test
    public void testSecretBytes(){
        final byte[] rndBytes = new byte[32];
        new SecureRandom().nextBytes(rndBytes);
        final SecretSignatureConfiguration secretSignatureConfiguration = new SecretSignatureConfiguration();
        final String base64Secret = Base64.encode(rndBytes).toString();
        secretSignatureConfiguration.setSecretBytes(rndBytes);
        assertEquals(base64Secret,secretSignatureConfiguration.getSecretBase64());
        assertTrue(Arrays.equals(secretSignatureConfiguration.getSecretBytes(),rndBytes));
    }

    @Test
    public void testSignVerifyBase64() throws JOSEException {
        final SecretSignatureConfiguration config = new SecretSignatureConfiguration();
        config.setSecretBase64(BASE64_512_BIT_SIG_SECRET);
        final JWTClaimsSet claims = new JWTClaimsSet.Builder().subject(VALUE).build();
        final SignedJWT signedJwt = config.sign(claims);
        assertTrue(config.verify(signedJwt));
    }

}
