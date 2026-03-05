package org.pac4j.oidc.metadata;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityType;
import com.nimbusds.openid.connect.sdk.federation.policy.language.PolicyViolationException;
import com.nimbusds.openid.connect.sdk.federation.trust.ResolveException;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChain;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChainResolver;
import com.nimbusds.openid.connect.sdk.federation.trust.TrustChainSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.exceptions.OidcConfigurationException;
import org.pac4j.oidc.exceptions.OidcException;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Resolves OpenID federation trust chains and OP metadata.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Slf4j
public class FederationChainResolver {

    public ResolutionResult resolve(final OidcConfiguration configuration) {
        val anchors = loadTrustAnchors(configuration);
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

    protected ResolutionResult getResolvedProviderMetadata(final TrustChain chain) {
        val leafStatement = chain.getLeafConfiguration();

        val rawMetadataJson = leafStatement.getClaimsSet().getMetadata(EntityType.OPENID_PROVIDER);
        if (rawMetadataJson == null) {
            throw new OidcException("No 'metadata' claim in the leaf Entity Statement");
        }

        try {
            val combinedPolicy = chain.resolveCombinedMetadataPolicy(EntityType.OPENID_PROVIDER);

            val resolvedJson = combinedPolicy.apply(rawMetadataJson);
            val metadata = OIDCProviderMetadata.parse(resolvedJson);
            val chainExpirationTime = new Date(chain.resolveExpirationTime().getTime() - 2 * 60 * 1000L);
            return new ResolutionResult(metadata, chainExpirationTime);
        } catch (final com.nimbusds.oauth2.sdk.ParseException | PolicyViolationException e) {
            throw new OidcException("Cannot resolve provider metadata via federation", e);
        }
    }

    protected Map<EntityID, JWKSet> loadTrustAnchors(final OidcConfiguration configuration) {
        val anchors = new HashMap<EntityID, JWKSet>();

        val trustAnchors = configuration.getFederation().getTrustAnchors();
        if (trustAnchors.size() == 0) {
            throw new OidcConfigurationException("No trust anchors defined");
        }

        for (val trustAnchor : trustAnchors) {
            val entity = new EntityID(trustAnchor.getTaIssuer());
            JWKSet jwks;
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

    public record ResolutionResult(OIDCProviderMetadata metadata, Date chainExpirationTime) {
    }
}
