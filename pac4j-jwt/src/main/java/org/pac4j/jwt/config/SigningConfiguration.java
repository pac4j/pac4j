package org.pac4j.jwt.config;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * Signing configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public interface SigningConfiguration {

    /**
     * Generate a signed JWT based on claims.
     *
     * @param claims the provided claims
     * @return the signed JWT
     */
    SignedJWT sign(JWTClaimsSet claims);
}
