package org.pac4j.oidc.config.method;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;

import java.security.PrivateKey;

/**
 * Contract for private_key_jwt client auth config.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Deprecated
public interface IPrivateKeyJwtClientAuthnMethodConfig {
    boolean isUseExpiration();
    JWSAlgorithm getJwsAlgorithm();
    PrivateKey getPrivateKey();
    String getKeyID();
    int getKeyClockSkew();
    long getValidity();
    JWK getJwk();
}
