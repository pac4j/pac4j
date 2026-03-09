package org.pac4j.oidc.metadata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.openid.connect.sdk.federation.registration.ClientRegistrationType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.HttpUtils;
import org.pac4j.core.util.InitializableObject;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.clientauth.ClientAuthenticationBuilder;
import org.pac4j.oidc.credentials.clientauth.DefaultClientAuthenticationBuilder;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.profile.creator.TokenValidator;
import org.pac4j.oidc.util.JwkHelper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Metadata resolver for federation (https://openid.net/specs/openid-federation-1_0.html).
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Slf4j
public class OidcFederationOpMetadataResolver extends InitializableObject implements IOidcOpMetadataResolver {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
        this.metadata = result.metadata();
        this.chainExpirationTime = result.chainExpirationTime();

        this.clientAuthenticationBuilder = new DefaultClientAuthenticationBuilder(this.configuration, this.metadata);
        this.clientAuthenticationBuilder.buildClientAuthentication();

        registerClient();

        this.tokenValidator = createTokenValidator();
    }

    protected FederationChainResolver.ResolutionResult resolveMetadata() {
        return federationChainResolver.resolve(configuration);
    }

    protected void registerClient() {
        val entityId = configuration.getFederation().getEntityId();
        val definedClientId = configuration.getClientId();
        if (StringUtils.isBlank(definedClientId)) {
            LOGGER.debug("ClientID is not defined");
            val registrationTypes = metadata.getClientRegistrationTypes();
            if (registrationTypes == null || registrationTypes.isEmpty()) {
                throw new OidcException("OP does not support any client registration types and RP clientID is not defined"
                    + " -> failing");
            }
            if (registrationTypes.contains(ClientRegistrationType.AUTOMATIC)) {
                LOGGER.debug("Automatic registration by OP -> setting clientId as entityId for further operation");
                configuration.setClientId(entityId);

            } else if (registrationTypes.contains(ClientRegistrationType.EXPLICIT)
                && !registrationTypes.contains(ClientRegistrationType.AUTOMATIC)) {

                val registrationEndpoint = metadata.getFederationRegistrationEndpointURI();
                if (registrationEndpoint == null) {
                    throw new OidcException("Client registration endpoint is not defined and "
                        + "only explicit registration is accepted by the OP");
                }
                LOGGER.debug("Registration endpoint exists and only explicit registration by OP -> performing explicit registration");

                val generator = configuration.getFederation().getEntityConfigurationGenerator();
                configuration.setOpMetadataResolver(this);
                val entityConfig = generator.generate();

                String clientId = null;
                HttpURLConnection connection = null;
                try {
                    val headers = new HashMap<String, String>();
                    headers.put(HttpConstants.CONTENT_TYPE_HEADER, generator.getContentType());
                    connection = HttpUtils.openPostConnection(registrationEndpoint.toURL(), headers);
                    HttpUtils.postBody(connection, entityConfig);
                    val code = connection.getResponseCode();
                    if (code == 200 || code == 201) {
                        val signedJwt = SignedJWT.parse(HttpUtils.readBody(connection));
                        val keys = JwkHelper.retrieveJwkSetFrom(metadata, null).getKeys();
                        var verified = false;
                        for (val key : keys) {
                            val signer = JwkHelper.determineVerifier(key, false);
                            if (signer.verify(signedJwt.getHeader(), signedJwt.getSigningInput(), signedJwt.getSignature())) {
                                verified = true;
                                break;
                            }
                        }
                        if (!verified) {
                            throw new OidcException("Cannot verify explicit registration response");
                        }
                        val payload = signedJwt.getPayload().toString();
                        val data = OBJECT_MAPPER.readTree(payload);
                        val orp = data.path("metadata").path("openid_relying_party");
                        clientId = orp.path("client_id").asText();
                        if (StringUtils.isNotBlank(clientId)) {
                            configuration.setClientId(clientId);
                            logSeparator();
                            logData(entityId, "id: [" + clientId + "]");
                            val clientSecret = orp.path("client_secret").asText();
                            if (StringUtils.isBlank(configuration.getSecret()) && StringUtils.isNotBlank(clientSecret)) {
                                configuration.setSecret(clientSecret);
                                logData(entityId, "secret: [" + clientSecret + "]");
                            }
                            logSeparator();
                            // renew client authentication
                            this.clientAuthenticationBuilder = new DefaultClientAuthenticationBuilder(this.configuration, this.metadata);
                            this.clientAuthenticationBuilder.buildClientAuthentication();
                        }
                    }
                    if (StringUtils.isBlank(clientId)) {
                        throw new OidcException("Cannot explicitely register the client (code=" + code + ")");
                    }
                } catch (final IOException | JOSEException | ParseException e) {
                    LOGGER.error("Explicit registration fails, no automatic option -> failing definitely");
                    throw new TechnicalException(e);
                } finally {
                    HttpUtils.closeConnection(connection);
                }
            }
        }
    }

    protected void logSeparator() {
        LOGGER.warn("/!\\ ================================================");
    }

    protected void logData(final String client, final String t) {
        LOGGER.warn("/!\\ Explicit registration of the client '{}' returns {}. This information won't be repeated. "
            + "You MUST add this value to your configuration before the next application startup!", client, t);
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
