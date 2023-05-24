package org.pac4j.jwt.config.encryption;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.*;
import lombok.val;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;
import org.pac4j.jwt.config.signature.SignatureConfiguration;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link SecretEncryptionConfiguration}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class SecretEncryptionConfigurationTests implements TestsConstants {

    private JWTClaimsSet buildClaims() {
        return new JWTClaimsSet.Builder().subject(VALUE).build();
    }

    @Test
    public void testMissingSecret() {
        val config = new SecretEncryptionConfiguration();
        TestsHelper.expectException(config::init, TechnicalException.class, "secret cannot be null");
    }

    @Test
    public void testMissingAlgorithm() {
        val config = new SecretEncryptionConfiguration(SECRET, null, EncryptionMethod.A128CBC_HS256);
        TestsHelper.expectException(config::init, TechnicalException.class, "algorithm cannot be null");
    }

    @Test
    public void testMissingMethod() {
        val config = new SecretEncryptionConfiguration(SECRET, JWEAlgorithm.DIR, null);
        TestsHelper.expectException(config::init, TechnicalException.class, "method cannot be null");
    }

    @Test
    public void testUnsupportedAlgorithm() {
        val config = new SecretEncryptionConfiguration(SECRET, JWEAlgorithm.ECDH_ES,
            EncryptionMethod.A128CBC_HS256);
        TestsHelper.expectException(config::init, TechnicalException.class,
            "Only the direct and AES algorithms are supported with the appropriate encryption method");
    }

    @Test
    public void testEncryptDecryptSignedJWT() throws ParseException, JOSEException {
        SignatureConfiguration macConfig = new SecretSignatureConfiguration(MAC_SECRET);
        val signedJWT = macConfig.sign(buildClaims());

        EncryptionConfiguration config = new SecretEncryptionConfiguration(MAC_SECRET);
        val token = config.encrypt(signedJWT);
        val encryptedJwt = (EncryptedJWT) JWTParser.parse(token);
        config.decrypt(encryptedJwt);
        val signedJWT2 = encryptedJwt.getPayload().toSignedJWT();
        assertEquals(VALUE, signedJWT2.getJWTClaimsSet().getSubject());
    }

    @Test
    public void testEncryptDecryptPlainJWT() throws ParseException, JOSEException {
        val config = new SecretEncryptionConfiguration(MAC_SECRET);
        config.setAlgorithm(JWEAlgorithm.A256GCMKW);
        config.setMethod(EncryptionMethod.A128GCM);

        final JWT jwt = new PlainJWT(buildClaims());
        val token = config.encrypt(jwt);
        val encryptedJwt = (EncryptedJWT) JWTParser.parse(token);
        config.decrypt(encryptedJwt);
        final JWT jwt2 = encryptedJwt;
        assertEquals(VALUE, jwt2.getJWTClaimsSet().getSubject());
    }


    @Test
    public void testEncryptDecryptPlainJWTBase64Secret() throws ParseException, JOSEException {
        val config = new SecretEncryptionConfiguration();
        config.setSecretBase64(BASE64_256_BIT_ENC_SECRET);

        config.setAlgorithm(JWEAlgorithm.A256GCMKW);
        config.setMethod(EncryptionMethod.A128GCM);

        final JWT jwt = new PlainJWT(buildClaims());
        val token = config.encrypt(jwt);
        val encryptedJwt = (EncryptedJWT) JWTParser.parse(token);
        config.decrypt(encryptedJwt);
        final JWT jwt2 = encryptedJwt;
        assertEquals(VALUE, jwt2.getJWTClaimsSet().getSubject());
    }


    @Test
    public void testEncryptDecryptPlainJWTBytesSecret() throws ParseException, JOSEException {
        val config = new SecretEncryptionConfiguration();
        config.setSecretBytes(new Base64(BASE64_256_BIT_ENC_SECRET).decode());

        config.setAlgorithm(JWEAlgorithm.A256GCMKW);
        config.setMethod(EncryptionMethod.A128GCM);

        final JWT jwt = new PlainJWT(buildClaims());
        val token = config.encrypt(jwt);
        val encryptedJwt = (EncryptedJWT) JWTParser.parse(token);
        config.decrypt(encryptedJwt);
        final JWT jwt2 = encryptedJwt;
        assertEquals(VALUE, jwt2.getJWTClaimsSet().getSubject());
    }
}
