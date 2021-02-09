package org.pac4j.jwt.config.encryption;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jwt.*;
import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.jwt.config.AbstractKeyEncryptionConfigurationTests;
import org.pac4j.jwt.config.signature.SecretSignatureConfiguration;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link ECEncryptionConfiguration}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class ECEncryptionConfigurationTests extends AbstractKeyEncryptionConfigurationTests {

    @Override
    protected String getAlgorithm() {
        return "EC";
    }

    private JWTClaimsSet buildClaims() {
        return new JWTClaimsSet.Builder().subject(VALUE).build();
    }

    @Test
    public void testMissingAlgorithm() {
        final ECEncryptionConfiguration config = new ECEncryptionConfiguration(buildKeyPair(), null, EncryptionMethod.A128CBC_HS256);
        TestsHelper.expectException(config::init, TechnicalException.class, "algorithm cannot be null");
    }

    @Test
    public void testMissingMethod() {
        final ECEncryptionConfiguration config = new ECEncryptionConfiguration(buildKeyPair(), JWEAlgorithm.ECDH_ES, null);
        TestsHelper.expectException(config::init, TechnicalException.class, "method cannot be null");
    }

    @Test
    public void testUnsupportedAlgorithm() {
        final ECEncryptionConfiguration config =
            new ECEncryptionConfiguration(buildKeyPair(), JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A128CBC_HS256);
        TestsHelper.expectException(config::init, TechnicalException.class,
            "Only Elliptic-curve algorithms are supported with the appropriate encryption method");
    }

    @Test
    public void testEncryptDecryptSignedJWT() throws ParseException, JOSEException {
        final SecretSignatureConfiguration macConfig = new SecretSignatureConfiguration(MAC_SECRET);
        final SignedJWT signedJWT = macConfig.sign(buildClaims());

        final ECEncryptionConfiguration config = new ECEncryptionConfiguration(buildKeyPair());
        config.setAlgorithm(JWEAlgorithm.ECDH_ES_A128KW);
        config.setMethod(EncryptionMethod.A192CBC_HS384);
        final String token = config.encrypt(signedJWT);
        final EncryptedJWT encryptedJwt = (EncryptedJWT) JWTParser.parse(token);
        config.decrypt(encryptedJwt);
        final SignedJWT signedJWT2 = encryptedJwt.getPayload().toSignedJWT();
        assertEquals(VALUE, signedJWT2.getJWTClaimsSet().getSubject());
    }

    @Test
    public void testEncryptDecryptPlainJWT() throws ParseException, JOSEException {
        final ECEncryptionConfiguration config = new ECEncryptionConfiguration(buildKeyPair());
        config.setAlgorithm(JWEAlgorithm.ECDH_ES_A256KW);
        config.setMethod(EncryptionMethod.A128GCM);

        final JWT jwt = new PlainJWT(buildClaims());
        final String token = config.encrypt(jwt);
        final EncryptedJWT encryptedJwt = (EncryptedJWT) JWTParser.parse(token);
        config.decrypt(encryptedJwt);
        final JWT jwt2 = encryptedJwt;
        assertEquals(VALUE, jwt2.getJWTClaimsSet().getSubject());
    }

    @Test
    public void testEncryptMissingKey() {
        final ECEncryptionConfiguration config = new ECEncryptionConfiguration();
        config.setAlgorithm(JWEAlgorithm.ECDH_ES_A256KW);
        config.setMethod(EncryptionMethod.A128GCM);

        final JWT jwt = new PlainJWT(buildClaims());
        TestsHelper.expectException(() -> config.encrypt(jwt), TechnicalException.class, "publicKey cannot be null");
    }

    @Test
    public void testDecryptMissingKey() throws ParseException {
        final ECEncryptionConfiguration config = new ECEncryptionConfiguration(buildKeyPair());
        config.setAlgorithm(JWEAlgorithm.ECDH_ES_A192KW);
        config.setMethod(EncryptionMethod.A128GCM);

        final JWT jwt = new PlainJWT(buildClaims());
        final String token = config.encrypt(jwt);
        final EncryptedJWT encryptedJwt = (EncryptedJWT) JWTParser.parse(token);
        final ECEncryptionConfiguration config2 = new ECEncryptionConfiguration();
        config2.setAlgorithm(JWEAlgorithm.ECDH_ES_A192KW);
        config2.setMethod(EncryptionMethod.A128GCM);
        TestsHelper.expectException(() -> config2.decrypt(encryptedJwt), TechnicalException.class, "privateKey cannot be null");
    }
}
