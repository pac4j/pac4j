package org.pac4j.oidc.federation.entity;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.oidc.client.OidcClient;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static org.pac4j.oidc.util.JwkHelper.*;

/**
 * The default entity configuration generator.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultEntityConfigurationGenerator extends InitializableObject implements EntityConfigurationGenerator {

    private static final String ENTITY_STATEMENT_TYPE = "entity-statement+jwt";

    public static final String CONTENT_TYPE = "application/" + ENTITY_STATEMENT_TYPE;
    private final OidcClient client;

    private String data;

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public String generate() {
        init();

        return data;
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        client.init(forceReinit);

        val config = client.getConfiguration();
        val federation = config.getFederation();
        JWK signingKey = null;
        val jwksProperties = federation.getJwks();
        val keystoreProperties = federation.getKeystore();
        if (jwksProperties != null && jwksProperties.getJwksResource() != null) {
            signingKey = loadCreateJwkFromJwks(jwksProperties);
        } else if (keystoreProperties != null && keystoreProperties.getKeystoreResource() != null) {
            signingKey = loadCreateJwkFromKeyStore(federation.getKeystore());
        } else {
            throw new TechnicalException("OIDC JWKS or keystore mandatory to generate the entity configuration");
        }

        data = buildConfig(signingKey);
    }

    protected String buildConfig(final JWK signingKey) {
        if (!hasPrivatePart(signingKey)) {
            throw new TechnicalException("Signing key must include private part");
        }

        val config = client.getConfiguration();
        val federation = config.getFederation();
        val callbackURL = client.getCallbackUrl();

        var entityId = federation.getEntityId();
        if (entityId == null) {
            entityId = callbackURL;
        }

        val now = new Date();
        long validityMs = (long) federation.getValidityInDays() * 24 * 60 * 60 * 1000L;
        val exp = new Date(now.getTime() + validityMs);

        val claimsBuilder = new JWTClaimsSet.Builder()
            .issuer(entityId)
            .subject(entityId)
            .audience(entityId)
            .jwtID(UUID.randomUUID().toString())
            .issueTime(now)
            .expirationTime(exp)
            .notBeforeTime(now);

        val rpMetadata = new LinkedHashMap<String, Object>();
        rpMetadata.put("redirect_uris", List.of(callbackURL));
        rpMetadata.put("application_type", federation.getApplicationType());
        rpMetadata.put("response_types", federation.getResponseTypes());
        rpMetadata.put("grant_types", federation.getGrantTypes());
        rpMetadata.put("scope", String.join(" ", federation.getScopes()));
        rpMetadata.put("token_endpoint_auth_method", federation.getClientAuthenticationMethod().getValue());

        val metadata = new LinkedHashMap<String, Object>();
        metadata.put("openid_relying_party", rpMetadata);

        claimsBuilder.claim("metadata", metadata);

        val publicKey = signingKey.toPublicJWK();
        val jwkSet = new JWKSet(publicKey);
        claimsBuilder.claim("jwks", jwkSet.toJSONObject());

        val claims = claimsBuilder.build();

        val alg = determineAlgorithm(signingKey, false);
        val header = new JWSHeader.Builder(alg)
            .type(new JOSEObjectType(ENTITY_STATEMENT_TYPE))
            .keyID(signingKey.getKeyID())
            .build();

        val signedJWT = new SignedJWT(header, claims);
        val signer = determineSigner(signingKey, false);
        try {
            signedJWT.sign(signer);
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }

        return signedJWT.serialize();
    }
}
