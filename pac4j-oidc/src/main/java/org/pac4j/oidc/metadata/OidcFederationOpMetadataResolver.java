package org.pac4j.oidc.metadata;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityType;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import com.nimbusds.openid.connect.sdk.federation.trust.ResolveException;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChain;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChainResolver;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChainSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.clientauth.ClientAuthenticationBuilder;
import org.pac4j.oidc.credentials.clientauth.DefaultClientAuthenticationBuilder;
import org.pac4j.oidc.exceptions.OidcConfigurationException;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.profile.creator.TokenValidator;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Metadata resolver for federation (https://openid.net/specs/openid-federation-1_0.html).
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Slf4j
@RequiredArgsConstructor
public class OidcFederationOpMetadataResolver extends InitializableObject implements IOidcOpMetadataResolver {

    private OIDCProviderMetadata metadata;

    protected ClientAuthenticationBuilder clientAuthenticationBuilder;

    protected TokenValidator tokenValidator;

    private final OidcConfiguration configuration;

    @Override
    protected void internalInit(boolean forceReinit) {
        this.metadata = retrieveMetadata();

        this.clientAuthenticationBuilder = new DefaultClientAuthenticationBuilder(this.configuration, this.metadata);
        this.clientAuthenticationBuilder.buildClientAuthentication();

        this.tokenValidator = createTokenValidator();
    }

    protected OIDCProviderMetadata retrieveMetadata() {
        val anchors = loadTrustAnchors();
        LOGGER.debug("Loaded {} trust anchor(s)", anchors.size());

        val resolver = new TrustChainResolver(anchors, configuration.getConnectTimeout(), configuration.getReadTimeout());

        val targetIssuer = new EntityID(configuration.getFederation().getTargetIssuer());
        LOGGER.debug("Target issuer: {}", targetIssuer);

        TrustChainSet resolvedChains;
        try {
            resolvedChains = resolver.resolveTrustChains(targetIssuer);
        } catch (final ResolveException e) {
            throw new OidcException(e);
        }
        LOGGER.debug("resolvedChains: {}", resolvedChains);

        if (resolvedChains.isEmpty()) {
            throw new OidcException("No valid trust chain found");
        }
        val chain = resolvedChains.getShortest();
        return getResolvedProviderMetadata(chain);
    }

    private OIDCProviderMetadata getResolvedProviderMetadata(final TrustChain chain) {
        val leafStatement = chain.getLeafConfiguration();

        val rawMetadataJson = leafStatement.getClaimsSet().getMetadata(EntityType.OPENID_PROVIDER);
        if (rawMetadataJson == null) {
            throw new IllegalStateException("Aucun claim 'metadata' dans l'Entity Statement du leaf");
        }

        try {
            val combinedPolicy = chain.resolveCombinedMetadataPolicy(EntityType.OPENID_PROVIDER);

            val resolvedJson = combinedPolicy.apply(rawMetadataJson);

            return OIDCProviderMetadata.parse(resolvedJson);
        } catch (final com.nimbusds.oauth2.sdk.ParseException | PolicyViolationException e) {
            throw new OidcException("Cannot resolve provider metadata via federation", e);
        }
    }

    private Map<EntityID, JWKSet> loadTrustAnchors() {
        val anchors = new HashMap<EntityID, JWKSet>();

        val trustAnchors = configuration.getFederation().getTrustAnchors();
        if (trustAnchors.size() == 0) {
            throw new OidcConfigurationException("No trust anchors defined");
        }

        for (val trustAnchor : trustAnchors) {
            val entity = new EntityID(trustAnchor.getTaIssuer());
            JWKSet jwks = null;
            try {
                jwks = JWKSet.load(new URL(trustAnchor.getTaJwksUrl()), configuration.getConnectTimeout(),
                    configuration.getReadTimeout(), 0);
            } catch (final IOException | ParseException e) {
                throw new TechnicalException(e);
            }
            anchors.put(entity, jwks);
        }

        return anchors;
    }

    protected TokenValidator createTokenValidator() {
        return new TokenValidator(this.configuration, this.metadata);
    }

    @Override
    public OIDCProviderMetadata load() {
        init();

        return metadata;
    }

    @Override
    public TokenValidator getTokenValidator() {
        init();

        return tokenValidator;
    }

    @Override
    public ClientAuthentication getClientAuthentication() {
        init();

        return clientAuthenticationBuilder.getClientAuthentication();
    }
}
