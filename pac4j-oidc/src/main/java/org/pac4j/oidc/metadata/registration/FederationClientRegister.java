package org.pac4j.oidc.metadata.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatementClaimsVerifier;
import com.nimbusds.openid.connect.sdk.federation.registration.ClientRegistrationType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.FileHelper;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.exceptions.OidcException;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Handles OpenID Federation client registration logic for OP in federation.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@Slf4j
public class FederationClientRegister {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public void register(final OidcConfiguration configuration, final OIDCProviderMetadata metadata, final JWKSet federationJWKS) {
        val entityId = configuration.getFederation().getEntityId();
        val definedClientId = configuration.getClientId();
        if (StringUtils.isNotBlank(definedClientId)) {
            return;
        }

        LOGGER.debug("ClientID is not defined");
        val opRegistrationTypes = metadata.getClientRegistrationTypes();
        if (opRegistrationTypes == null || opRegistrationTypes.isEmpty()) {
            throw new OidcException("OP does not support any client registration types and RP clientID is not defined -> failing");
        }

        val rpRegistrationTypes = configuration.getFederation().getClientRegistrationTypes();
        if (opRegistrationTypes.contains(ClientRegistrationType.AUTOMATIC)
            && rpRegistrationTypes.contains(ClientRegistrationType.AUTOMATIC.getValue())) {
            LOGGER.debug("Automatic registration by OP (supported by RP) -> setting clientId as entityId for further operation");
            configuration.setClientId(entityId);
        } else if (opRegistrationTypes.contains(ClientRegistrationType.EXPLICIT)
            && rpRegistrationTypes.contains(ClientRegistrationType.EXPLICIT.getValue())) {
            performExplicitRegistration(configuration, metadata, entityId, federationJWKS);
        } else {
            LOGGER.warn("Registration via federation is skipped due to OP/RP configuration");
        }
    }

    protected void performExplicitRegistration(final OidcConfiguration configuration,
                                               final OIDCProviderMetadata metadata,
                                               final String entityId,
                                               final JWKSet federationJWKS) {
        val registrationEndpoint = metadata.getFederationRegistrationEndpointURI();
        if (registrationEndpoint == null) {
            throw new OidcException("Client registration endpoint is not defined and only explicit registration is accepted by the OP");
        }
        LOGGER.debug("Registration endpoint exists and only explicit registration by OP (and RP) -> performing explicit registration");

        val generator = configuration.getFederation().getEntityConfigurationGenerator();
        val entityConfig = generator.generateEntityStatement();

        String clientId = null;
        try {
            val request = new HTTPRequest(HTTPRequest.Method.POST, registrationEndpoint);
            request.setContentType("application/jose");
            request.setBody(entityConfig);
            configuration.configureHttpRequest(request);
            val response = request.send();
            val code = response.getStatusCode();
            val error = response.getStatusMessage();
            val content = response.getContent();
            if (code == 200 || code == 201) {
                LOGGER.debug("Received response registration: {}", content);
                val statement = EntityStatement.parse(content);
                val type = statement.getSignedStatement().getHeader().getType();
                if (type != null && !JOSEObjectType.JWT.equals(type) && !EntityStatement.JOSE_OBJECT_TYPE.equals(type)) {
                    throw new OidcException("Unexpected explicit registration response typ: " + type);
                }
                statement.verifySignature(federationJWKS);
                // jwks property is optional in the registration response
                val claimsVerifier = new EntityStatementClaimsVerifier(new Audience(entityId));
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
                    LOGGER.warn("/!\\ Explicit registration of the client '{}' returns id: [{}]. This information won't be repeated. "
                        + "You MUST add this value to your configuration before the next application startup!", entityId, clientId);
                    val clientSecret = orp.path("client_secret").asText();
                    if (StringUtils.isNotBlank(clientSecret)) {
                        val secretExportFile = configuration.getFederation().getSecretExportFile();
                        if (isBlank(secretExportFile)) {
                            throw new OidcException("Client secret export file is required");
                        }
                        LOGGER.warn("/!\\ The received secret has been saved into the file: {}", secretExportFile);
                        val path = Path.of(secretExportFile);
                        FileHelper.savePrivateFile(path, clientSecret);
                    }
                    logSeparator();
                }
            }
            if (isBlank(clientId)) {
                throw new OidcException("Cannot explicitely register the client " + entityId
                    + "(code=" + code + ",error=" + error + ",content=" + content + ")");
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
}
