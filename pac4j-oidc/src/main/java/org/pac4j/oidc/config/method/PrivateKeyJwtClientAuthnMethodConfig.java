package org.pac4j.oidc.config.method;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.AsymmetricJWK;
import com.nimbusds.jose.jwk.JWK;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.oidc.util.JwkHelper;

import java.security.PrivateKey;

/**
 * Config for the private_key_jwt client authn.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@RequiredArgsConstructor
public class PrivateKeyJwtClientAuthnMethodConfig extends InitializableObject implements IPrivateKeyJwtClientAuthnMethodConfig {

    @Getter
    private final JwksProperties jwks;

    /** Default JWT token expiration time in seconds */
    @Getter
    @Setter
    private long validity = 60;
    /** Clock skew used to not reuse a token to close to expire */
    @Getter
    @Setter
    private int keyClockSkew = 10;
    /** Manage expiration of private key JWT when true */
    @Getter
    @Setter
    private boolean useExpiration = true;

    private JWK jwk;

    @Override
    public JWSAlgorithm getJwsAlgorithm() {
        init();

        return JwkHelper.determineAlgorithm(jwk, false);
    }

    @Override
    public PrivateKey getPrivateKey() {
        init();

        try {
            return ((AsymmetricJWK) jwk).toKeyPair().getPrivate();
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    public String getKeyID() {
        init();

        return jwk.getKeyID();
    }

    @Override
    public JWK getJwk() {
        init();

        return jwk;
    }

    @Override
    protected void internalInit(boolean forceReinit) {
        jwk = JwkHelper.loadJwkFromOrCreateJwks(jwks);
    }
}
