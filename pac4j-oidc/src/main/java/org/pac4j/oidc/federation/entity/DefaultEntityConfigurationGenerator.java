package org.pac4j.oidc.federation.entity;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.oidc.client.OidcClient;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.JwkHelper.*;

/**
 * The default entity configuration generator.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultEntityConfigurationGenerator extends InitializableObject implements EntityConfigurationGenerator {

    public static final String ENTITY_STATEMENT_TYPE = "entity-statement+jwt";

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
        val config = client.getConfiguration();
        val federation = config.getFederation();
        JWK signingKey = null;
        val jwksProperties = federation.getJwks();
        val keystoreProperties = federation.getKeystore();
        if (jwksProperties != null && jwksProperties.getJwksResource() != null) {
            signingKey = loadJwkFromOrCreateJwks(jwksProperties);
        } else if (keystoreProperties != null && keystoreProperties.getKeystoreResource() != null) {
            signingKey = loadJwkFromOrCreateKeyStore(federation.getKeystore());
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
        val callbackURL = client.computeFinalCallbackUrl(null);
        var entityId = federation.getEntityId();
        if (StringUtils.isBlank(entityId)) {
            entityId = client.getCallbackUrl();
            federation.setEntityId(entityId);
        }
        assertNotBlank("entityId", entityId);
        LOGGER.info("Generating entity configuration for: {}", entityId);

        val now = new Date();
        long validityMs = (long) federation.getValidityInDays() * 24 * 60 * 60 * 1000L;
        val exp = new Date(now.getTime() + validityMs);

        val claimsBuilder = new JWTClaimsSet.Builder()
            .issuer(entityId)
            .subject(entityId)
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
        val clientAuth = config.getClientAuthenticationMethod();
        if (clientAuth != null) {
            rpMetadata.put("token_endpoint_auth_method", clientAuth.getValue());
            if (clientAuth == ClientAuthenticationMethod.PRIVATE_KEY_JWT) {
                val clientAuthConfig = config.getPrivateKeyJwtClientAuthnMethodConfig();
                if (clientAuthConfig != null && clientAuthConfig.getJwsAlgorithm() != null) {
                    rpMetadata.put("token_endpoint_auth_signing_alg", clientAuthConfig.getJwsAlgorithm().getName());
                    val publicKey = clientAuthConfig.getJwk().toPublicJWK();
                    val jwkSet = new JWKSet(publicKey);
                    rpMetadata.put("jwks", jwkSet.toJSONObject());
                }
            }
        }
        val requestObjectSigningAlg = config.getRequestObjectSigningAlgorithm();
        if (requestObjectSigningAlg != null) {
            rpMetadata.put("request_object_signing_alg", requestObjectSigningAlg.getName());
        }
        rpMetadata.put("client_registration_types", federation.getClientRegistrationTypes());
        rpMetadata.put("client_name", federation.getContactName());
        val contacts = federation.getContactEmails();
        if (contacts != null && contacts.size() > 0) {
            rpMetadata.put("contacts", contacts);
        }

        val metadata = new LinkedHashMap<String, Object>();
        metadata.put("openid_relying_party", rpMetadata);

        claimsBuilder.claim("metadata", metadata);

        val publicKey = signingKey.toPublicJWK();
        val jwkSet = new JWKSet(publicKey);
        claimsBuilder.claim("jwks", jwkSet.toJSONObject());

        val trustAnchors = federation.getTrustAnchors();
        if (trustAnchors != null && trustAnchors.size() > 0) {
            claimsBuilder.claim("authority_hints", trustAnchors.stream().map(ta -> ta.getIssuer()).collect(Collectors.toList()));
        }

        val claims = claimsBuilder.build();

        return buildSignedJwt(claims, signingKey, ENTITY_STATEMENT_TYPE);
    }
}
