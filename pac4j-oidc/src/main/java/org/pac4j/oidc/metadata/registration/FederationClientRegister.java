package org.pac4j.oidc.metadata.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatementClaimsVerifier;
import com.nimbusds.openid.connect.sdk.federation.registration.ClientRegistrationType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.util.OidcHelper;

import java.io.IOException;
import java.text.ParseException;

/**
 * Handles OpenID Federation client recording / registration logic for OP metadata resolution.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Slf4j
public class FederationClientRegister {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public void register(final OidcConfiguration configuration, final OIDCProviderMetadata metadata) {
        val entityId = configuration.getFederation().getEntityId();
        val definedClientId = configuration.getClientId();
        if (StringUtils.isNotBlank(definedClientId)) {
            return;
        }

        LOGGER.debug("ClientID is not defined");
        val registrationTypes = metadata.getClientRegistrationTypes();
        if (registrationTypes == null || registrationTypes.isEmpty()) {
            throw new OidcException("OP does not support any client registration types and RP clientID is not defined -> failing");
        }
        if (registrationTypes.contains(ClientRegistrationType.AUTOMATIC)) {
            LOGGER.debug("Automatic registration by OP -> setting clientId as entityId for further operation");
            configuration.setClientId(entityId);
            return;
        }
        if (registrationTypes.contains(ClientRegistrationType.EXPLICIT)) {
            explicitRegistration(configuration, metadata, entityId);
        }
    }

    protected void explicitRegistration(final OidcConfiguration configuration,
                                        final OIDCProviderMetadata metadata,
                                        final String entityId) {
        val registrationEndpoint = metadata.getFederationRegistrationEndpointURI();
        if (registrationEndpoint == null) {
            throw new OidcException("Client registration endpoint is not defined and only explicit registration is accepted by the OP");
        }
        LOGGER.debug("Registration endpoint exists and only explicit registration by OP -> performing explicit registration");

        val generator = configuration.getFederation().getEntityConfigurationGenerator();
        val entityConfig = generator.generate();

        String clientId = null;
        try {
            val request = new HTTPRequest(HTTPRequest.Method.POST, registrationEndpoint);
            request.setContentType(generator.getContentType());
            request.setBody(entityConfig);
            configuration.configureHttpRequest(request);
            val response = request.send();
            val code = response.getStatusCode();
            if (code == 200 || code == 201) {
                val statement = EntityStatement.parse(response.getContent());
                val type = statement.getSignedStatement().getHeader().getType();
                if (type != null && !JOSEObjectType.JWT.equals(type) && !EntityStatement.JOSE_OBJECT_TYPE.equals(type)) {
                    throw new OidcException("Unexpected explicit registration response typ: " + type);
                }
                statement.verifySignature(OidcHelper.retrieveJwkSetFrom(metadata, null));
                val claimsVerifier = new EntityStatementClaimsVerifier();
                claimsVerifier.verify(statement.getSignedStatement().getJWTClaimsSet(), null);
                val expectedIssuer = metadata.getIssuer().getValue();
                val issuer = statement.getClaimsSet().getIssuerEntityID();
                if (issuer == null || !expectedIssuer.equals(issuer.getValue())) {
                    throw new OidcException("Unexpected explicit registration response issuer: " + issuer);
                }
                val payload = statement.getSignedStatement().getPayload().toString();
                val data = OBJECT_MAPPER.readTree(payload);
                val orp = data.path("metadata").path("openid_relying_party");
                clientId = orp.path("client_id").asText();
                if (StringUtils.isNotBlank(clientId)) {
                    configuration.setClientId(clientId);
                    logSeparator();
                    logData(entityId, "id: [" + clientId + "]");
                    val clientSecret = orp.path("client_secret").asText();
                    if (StringUtils.isBlank(configuration.getSecret()) && StringUtils.isNotBlank(clientSecret)
                        && !ClientAuthenticationMethod.PRIVATE_KEY_JWT.equals(configuration.getClientAuthenticationMethod())) {
                        configuration.setSecret(clientSecret);
                        LOGGER.warn("/!\\ Explicit registration returned a client secret for client '{}'. "
                            + "The secret has been set in-memory for this run and MUST be persisted in your configuration.", entityId);
                    }
                    logSeparator();
                }
            }
            if (StringUtils.isBlank(clientId)) {
                throw new OidcException("Cannot explicitely register the client (code=" + code + ")");
            }
        } catch (final IOException | JOSEException | BadJOSEException | ParseException
                       | com.nimbusds.oauth2.sdk.ParseException e) {
            LOGGER.error("Explicit registration fails, no automatic option -> failing definitely");
            throw new TechnicalException(e);
        }
    }

    protected void logSeparator() {
        LOGGER.warn("/!\\ ================================================");
    }

    protected void logData(final String client, final String data) {
        LOGGER.warn("/!\\ Explicit registration of the client '{}' returns {}. This information won't be repeated. "
            + "You MUST add this value to your configuration before the next application startup!", client, data);
    }
}
