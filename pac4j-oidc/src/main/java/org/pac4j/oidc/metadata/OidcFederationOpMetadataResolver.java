package org.pac4j.oidc.metadata;

import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.clientauth.ClientAuthenticationBuilder;
import org.pac4j.oidc.credentials.clientauth.DefaultClientAuthenticationBuilder;
import org.pac4j.oidc.profile.creator.TokenValidator;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * Metadata resolver for federation (https://openid.net/specs/openid-federation-1_0.html).
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Slf4j
public class OidcFederationOpMetadataResolver extends InitializableObject implements IOidcOpMetadataResolver {

    private volatile OIDCProviderMetadata metadata;

    protected volatile ClientAuthenticationBuilder clientAuthenticationBuilder;

    protected volatile TokenValidator tokenValidator;

    private final OidcConfiguration configuration;

    private final FederationChainResolver federationChainResolver = new FederationChainResolver();

    private volatile boolean backgroundReloadInProgress;

    @Setter(AccessLevel.PROTECTED)
    private volatile Date chainExpirationTime;

    public OidcFederationOpMetadataResolver(final OidcConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        if (metadata == null) {
            LOGGER.debug("Blocking load of the provider metadata via federation");
            reloadSynchronously();
        } else {
            startBackgroundReload();
        }
    }

    @Override
    protected boolean shouldInitialize(final boolean forceReinit) {
        if (backgroundReloadInProgress && metadata != null) {
            return false;
        }
        if (metadata == null || chainExpirationTime == null || new Date().after(chainExpirationTime)) {
            return true;
        }
        return super.shouldInitialize(forceReinit);
    }

    protected synchronized void startBackgroundReload() {
        if (backgroundReloadInProgress) {
            return;
        }
        backgroundReloadInProgress = true;
        LOGGER.debug("Starting background refresh provider metadata via federation");
        CompletableFuture.runAsync(() -> {
            try {
                reloadSynchronously();
            } catch (final Exception e) {
                LOGGER.warn("Cannot refresh provider metadata via federation", e);
            } finally {
                backgroundReloadInProgress = false;
            }
        });
    }

    protected void reloadSynchronously() {
        val result = resolveMetadata();
        val resolvedMetadata = result.metadata();
        val resolvedChainExpirationTime = result.chainExpirationTime();
        val resolvedClientAuthenticationBuilder = new DefaultClientAuthenticationBuilder(this.configuration, resolvedMetadata);
        resolvedClientAuthenticationBuilder.buildClientAuthentication();

        this.metadata = resolvedMetadata;
        this.chainExpirationTime = resolvedChainExpirationTime;
        this.clientAuthenticationBuilder = resolvedClientAuthenticationBuilder;
        this.tokenValidator = createTokenValidator();
    }
    protected FederationChainResolver.ResolutionResult resolveMetadata() {
        return federationChainResolver.resolve(configuration);
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
