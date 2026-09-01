package org.pac4j.jwt.config.signature;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * Signature configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public interface SignatureConfiguration {

    /**
     * Whether this signature configuration supports this algorithm.
     *
     * @param algorithm the signature algorithm
     * @return whether this signature configuration supports this algorithm
     */
    boolean supports(JWSAlgorithm algorithm);

    /**
     * Generate a signed JWT based on claims.
     *
     * @param claims the provided claims
     * @return the signed JWT
     */
    SignedJWT sign(JWTClaimsSet claims);

    /**
     * Generate a signed JWT based on a header and claims, for the protocols requiring specific header
     * parameters: a "typ", a "kid", the certificate chain in "x5c"...
     *
     * <p>The algorithm of the header must be the one of this configuration.</p>
     *
     * @param header the provided header
     * @param claims the provided claims
     * @return the signed JWT
     * @since 6.6.0
     */
    default SignedJWT sign(JWSHeader header, JWTClaimsSet claims) {
        throw new UnsupportedOperationException("signing with a specific header is not supported by " + getClass().getName());
    }

    /**
     * Verify a signed JWT.
     *
     * @param jwt the signed JWT
     * @return whether the signed JWT is verified
     * @throws JOSEException exception when verifying the JWT
     */
    boolean verify(SignedJWT jwt) throws JOSEException;
}
