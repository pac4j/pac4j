package org.pac4j.oidc.federation.entity;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.client.config.KeystoreProperties;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.keystore.loading.KeyStoreUtils;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.oidc.federation.config.OidcFederationProperties;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.KeyStoreException;
import java.text.ParseException;

/**
 * The default entity configuration generator.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultEntityConfigurationGenerator extends InitializableObject implements EntityConfigurationGenerator {

    private final OidcFederationProperties properties;

    private String data;

    @Override
    public String generate() {
        init();

        return data;
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        JWK signingKey = null;
        val jwks = properties.getJwksResource();
        if (jwks != null) {
            signingKey = loadFromJWKS(jwks);
        } else {
            signingKey = loadFromKeyStore(properties.getKeystore());
        }
        System.out.println(signingKey);
    }

    private JWK loadFromJWKS(final Resource jwks) {
        if (!jwks.exists()) {
            if (!jwks.isFile()) {
                throw new TechnicalException("Cannot create JWKS resource which is not a file: " + jwks);
            }
            // TODO
            System.out.println("Generate JWKS");
        }
        try (val is = jwks.getInputStream()) {
            val jwkSet = JWKSet.load(is);

            val signingJwk = jwkSet.getKeys().stream()
                .filter(k -> KeyUse.SIGNATURE.equals(k.getKeyUse()))
                .filter(JWK::isPrivate)
                .findFirst()
                .orElseThrow(() -> new TechnicalException("No private key for signature"));

            return signingJwk;

        } catch (final IOException | ParseException e) {
            throw new TechnicalException(e);
        }
    }

    private JWK loadFromKeyStore(final KeystoreProperties keystoreProperties) {
        val keystoreResource = keystoreProperties.getKeystoreResource();
        if (keystoreResource != null) {
            val keystoreGenerator = keystoreProperties.getKeystoreGenerator();
            if (keystoreGenerator.shouldGenerate()) {
                LOGGER.info("Generating keystore for resource: {}", keystoreProperties.getKeystoreResource());
                keystoreGenerator.generate();
            }
            val keyStoreAndAlias = KeyStoreUtils.retrieveKeyStoreAndAlias(keystoreProperties);
            val keyStore = keyStoreAndAlias.getLeft();
            val alias = keyStoreAndAlias.getRight();
            try {
                return JWK.load(keyStore, alias, keystoreProperties.getPrivateKeyPassword().toCharArray());
            } catch (final KeyStoreException | JOSEException e) {
                throw new TechnicalException(e);
            }
        } else {
            throw new TechnicalException("OIDC JWKS or keystore mandatory to generate the entity configuration");
        }
    }
}
