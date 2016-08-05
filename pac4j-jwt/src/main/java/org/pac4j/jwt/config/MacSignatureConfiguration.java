package org.pac4j.jwt.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

/**
 * HMac signature configuration: http://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-hmac
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class MacSignatureConfiguration extends InitializableObject implements SignatureConfiguration {

    private String secret;

    private JWSAlgorithm algorithm = JWSAlgorithm.HS256;

    public MacSignatureConfiguration() {}

    public MacSignatureConfiguration(final String secret) {
        this.secret = secret;
    }

    public MacSignatureConfiguration(final String secret, final JWSAlgorithm algorithm) {
        this.secret = secret;
        this.algorithm = algorithm;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("algorithm", algorithm);
        CommonHelper.assertNotBlank("secret", secret);

        if (!supports(this.algorithm)) {
            throw new TechnicalException("Only the HS256, HS384 and HS512 algorithms are supported for HMac signature");
        }
    }

    @Override
    public boolean supports(final JWSAlgorithm algorithm) {
        return algorithm != null && MACVerifier.SUPPORTED_ALGORITHMS.contains(algorithm);
    }

    @Override
    public SignedJWT sign(final JWTClaimsSet claims) {
        init();

        try {
            final JWSSigner signer = new MACSigner(this.secret);
            final SignedJWT signedJWT = new SignedJWT(new JWSHeader(algorithm), claims);
            signedJWT.sign(signer);
            return signedJWT;
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    public boolean verify(final SignedJWT jwt) throws JOSEException {
        init();

        final JWSVerifier verifier = new MACVerifier(this.secret);
        return jwt.verify(verifier);
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public static MacSignatureConfiguration buildFromJwk(final String json) {
        CommonHelper.assertNotBlank("json", json);

        try {
            final OctetSequenceKey octetSequenceKey = OctetSequenceKey.parse(json);
            return new MacSignatureConfiguration(new String(octetSequenceKey.toByteArray(), "UTF-8"));
        } catch (final UnsupportedEncodingException | ParseException e) {
            throw new TechnicalException(e);
        }
    }

    public JWSAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(final JWSAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "secret", "[protected]", "algorithm", algorithm);
    }
}
