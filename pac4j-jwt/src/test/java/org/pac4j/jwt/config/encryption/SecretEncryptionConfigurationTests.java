package org.pac4j.jwt.config.encryption;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.*;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;

import java.text.ParseException;

import static org.junit.Assert.*;

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
        final var config = new SecretEncryptionConfiguration();
        TestsHelper.expectException(config::init, TechnicalException.class, "secret cannot be null");
    }

    @Test
    public void testMissingAlgorithm() {
        final var config = new SecretEncryptionConfiguration(SECRET, null, EncryptionMethod.A128CBC_HS256);
        TestsHelper.expectException(config::init, TechnicalException.class, "algorithm cannot be null");
    }

    @Test
    public void testMissingMethod() {
        final var config = new SecretEncryptionConfiguration(SECRET, JWEAlgorithm.DIR, null);
        TestsHelper.expectException(config::init, TechnicalException.class, "method cannot be null");
    }

    @Test
    public void testUnsupportedAlgorithm() {
        final var config = new SecretEncryptionConfiguration(SECRET, JWEAlgorithm.ECDH_ES,
            EncryptionMethod.A128CBC_HS256);
        TestsHelper.expectException(config::init, TechnicalException.class,
            "Only the direct and AES algorithms are supported with the appropriate encryption method");
    }

    @Test
    public void testEncryptDecryptSignedJWT() throws ParseException, JOSEException {
        final var macConfig = new SecretSignatureConfiguration(MAC_SECRET);
        final var signedJWT = macConfig.sign(buildClaims());

        final var config = new SecretEncryptionConfiguration(MAC_SECRET);
        final var token = config.encrypt(signedJWT);
        final var encryptedJwt = (EncryptedJWT) JWTParser.parse(token);
        config.decrypt(encryptedJwt);
        final var signedJWT2 = encryptedJwt.getPayload().toSignedJWT();
        assertEquals(VALUE, signedJWT2.getJWTClaimsSet().getSubject());
    }

    @Test
    public void testEncryptDecryptPlainJWT() throws ParseException, JOSEException {
        final var config = new SecretEncryptionConfiguration(MAC_SECRET);
        config.setAlgorithm(JWEAlgorithm.A256GCMKW);
        config.setMethod(EncryptionMethod.A128GCM);

        final JWT jwt = new PlainJWT(buildClaims());
        final var token = config.encrypt(jwt);
        final var encryptedJwt = (EncryptedJWT) JWTParser.parse(token);
        config.decrypt(encryptedJwt);
        final JWT jwt2 = encryptedJwt;
        assertEquals(VALUE, jwt2.getJWTClaimsSet().getSubject());
    }


    @Test
    public void testEncryptDecryptPlainJWTBase64Secret() throws ParseException, JOSEException {
        final var config = new SecretEncryptionConfiguration();
        config.setSecretBase64(BASE64_256_BIT_ENC_SECRET);

        config.setAlgorithm(JWEAlgorithm.A256GCMKW);
        config.setMethod(EncryptionMethod.A128GCM);

        final JWT jwt = new PlainJWT(buildClaims());
        final var token = config.encrypt(jwt);
        final var encryptedJwt = (EncryptedJWT) JWTParser.parse(token);
        config.decrypt(encryptedJwt);
        final JWT jwt2 = encryptedJwt;
        assertEquals(VALUE, jwt2.getJWTClaimsSet().getSubject());
    }


    @Test
    public void testEncryptDecryptPlainJWTBytesSecret() throws ParseException, JOSEException {
        final var config = new SecretEncryptionConfiguration();
        config.setSecretBytes(new Base64(BASE64_256_BIT_ENC_SECRET).decode());

        config.setAlgorithm(JWEAlgorithm.A256GCMKW);
        config.setMethod(EncryptionMethod.A128GCM);

        final JWT jwt = new PlainJWT(buildClaims());
        final var token = config.encrypt(jwt);
        final var encryptedJwt = (EncryptedJWT) JWTParser.parse(token);
        config.decrypt(encryptedJwt);
        final JWT jwt2 = encryptedJwt;
        assertEquals(VALUE, jwt2.getJWTClaimsSet().getSubject());
    }
}
