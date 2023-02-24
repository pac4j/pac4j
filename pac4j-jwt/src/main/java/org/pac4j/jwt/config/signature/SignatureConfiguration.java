package org.pac4j.jwt.config.signature;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
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
     * Verify a signed JWT.
     *
     * @param jwt the signed JWT
     * @return whether the signed JWT is verified
     * @throws com.nimbusds.jose.JOSEException exception when verifying the JWT
     */
    boolean verify(SignedJWT jwt) throws JOSEException;
}
