package org.pac4j.oidc.metadata;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.clientauth.ClientAuthenticationBuilder;
import org.pac4j.oidc.credentials.clientauth.DefaultClientAuthenticationBuilder;
import org.pac4j.oidc.metadata.chain.FederationChainResolver;
import org.pac4j.oidc.metadata.registration.FederationClientRegister;
import org.pac4j.oidc.profile.creator.TokenValidator;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Metadata resolver for federation (https://openid.net/specs/openid-federation-1_0.html).
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Slf4j
public class OidcFederationOpMetadataResolver extends InitializableObject implements IOidcOpMetadataResolver {

    private static final Executor FEDERATION_REFRESH_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
        val thread = new Thread(r, "pac4j-oidc-federation-refresh");
        thread.setDaemon(true);
        return thread;
    });

    private volatile OIDCProviderMetadata metadata;

    protected volatile ClientAuthenticationBuilder clientAuthToken;

    protected volatile ClientAuthenticationBuilder clientAuthPar;

    protected volatile TokenValidator tokenValidator;

    @Getter
    protected volatile List<String> trustChain;

    private final OidcConfiguration configuration;

    private final FederationChainResolver federationChainResolver = new FederationChainResolver();
    private final FederationClientRegister federationClientRegister = new FederationClientRegister();

    private volatile boolean backgroundReloadInProgress;

    @Setter(AccessLevel.PROTECTED)
    private volatile Date chainExpirationTime;

    private volatile JWKSet federationJWKS;

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
        }, FEDERATION_REFRESH_EXECUTOR);
    }

    protected void reloadSynchronously() {
        val result = resolveMetadata();
        this.metadata = result.metadata();
        this.chainExpirationTime = result.expirationTime();
        this.federationJWKS = result.federationJWKS();
        this.trustChain = result.trustChain();

        registerClient();

        this.clientAuthToken = new DefaultClientAuthenticationBuilder(configuration, metadata, metadata.getTokenEndpointURI());
        this.clientAuthToken.buildClientAuthentication();

        this.clientAuthPar = new DefaultClientAuthenticationBuilder(configuration, metadata,
            metadata.getPushedAuthorizationRequestEndpointURI());
        this.clientAuthPar.buildClientAuthentication();

        this.tokenValidator = createTokenValidator();
    }

    protected FederationChainResolver.ResolutionResult resolveMetadata() {
        return federationChainResolver.resolve(configuration);
    }

    protected void registerClient() {
        federationClientRegister.register(configuration, metadata, federationJWKS);
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
    public ClientAuthentication getClientAuthenticationTokenEndpoint() {
        init();
        return clientAuthToken.getClientAuthentication();
    }

    @Override
    public ClientAuthentication getClientAuthenticationPAREndpoint() {
        init();
        return clientAuthPar.getClientAuthentication();
    }
}
