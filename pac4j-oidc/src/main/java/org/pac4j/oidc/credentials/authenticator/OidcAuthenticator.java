package org.pac4j.oidc.credentials.authenticator;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * The OpenID Connect authenticator.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcAuthenticator extends InitializableWebObject implements Authenticator<OidcCredentials> {

    private static final Logger logger = LoggerFactory.getLogger(OidcAuthenticator.class);

    private OidcConfiguration configuration;

    protected ClientAuthentication clientAuthentication;

    public OidcAuthenticator() { }

    public OidcAuthenticator(final OidcConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", configuration);

        configuration.init(context);

        // check authentication methods
        final List<ClientAuthenticationMethod> metadataMethods = configuration.getProviderMetadata().getTokenEndpointAuthMethods();

        final ClientAuthenticationMethod chosenMethod;
        final ClientAuthenticationMethod configurationMethod = configuration.getClientAuthenticationMethod();
        if (CommonHelper.isNotEmpty(metadataMethods)) {
            if (metadataMethods.contains(configurationMethod)) {
                chosenMethod = configurationMethod;
            } else {
                chosenMethod = metadataMethods.get(0);
                logger.warn("Preferred token endpoint Authentication method: {} not available. Defaulting to: {}",
                        configurationMethod, chosenMethod);
            }
        } else {
            chosenMethod = ClientAuthenticationMethod.getDefault();
            logger.warn("Provider metadata does not provide Token endpoint authentication methods. Defaulting to: {}",
                    chosenMethod);
        }

        final ClientID _clientID = new ClientID(configuration.getClientId());
        final Secret _secret = new Secret(configuration.getSecret());
        if (ClientAuthenticationMethod.CLIENT_SECRET_POST.equals(chosenMethod)) {
            clientAuthentication = new ClientSecretPost(_clientID, _secret);
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(chosenMethod)) {
            clientAuthentication = new ClientSecretBasic(_clientID, _secret);
        } else {
            throw new TechnicalException("Unsupported client authentication method: " + chosenMethod);
        }
    }

    @Override
    public void validate(final OidcCredentials credentials, final WebContext context) throws HttpAction {
        init(context);

        final AuthorizationCode code = credentials.getCode();
        // if we have a code
        if (code != null) {
            try {
                // Token request
                final TokenRequest request = new TokenRequest(configuration.getProviderMetadata().getTokenEndpointURI(), this.clientAuthentication,
                        new AuthorizationCodeGrant(code, new URI(configuration.getCallbackUrl())));
                HTTPRequest tokenHttpRequest = request.toHTTPRequest();
                tokenHttpRequest.setConnectTimeout(configuration.getConnectTimeout());
                tokenHttpRequest.setReadTimeout(configuration.getReadTimeout());

                final HTTPResponse httpResponse = tokenHttpRequest.send();
                logger.debug("Token response: status={}, content={}", httpResponse.getStatusCode(),
                        httpResponse.getContent());

                final TokenResponse response = OIDCTokenResponseParser.parse(httpResponse);
                if (response instanceof TokenErrorResponse) {
                    throw new TechnicalException("Bad token response, error=" + ((TokenErrorResponse) response).getErrorObject());
                }
                logger.debug("Token response successful");
                final OIDCTokenResponse tokenSuccessResponse = (OIDCTokenResponse) response;

                // save tokens in credentials
                final OIDCTokens oidcTokens = tokenSuccessResponse.getOIDCTokens();
                credentials.setAccessToken(oidcTokens.getAccessToken());
                credentials.setRefreshToken(oidcTokens.getRefreshToken());
                credentials.setIdToken(oidcTokens.getIDToken());

            } catch (final URISyntaxException | IOException | ParseException e) {
                throw new TechnicalException(e);
            }
        }
    }

    public OidcConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(OidcConfiguration configuration) {
        this.configuration = configuration;
    }

    public ClientAuthentication getClientAuthentication() {
        return clientAuthentication;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", configuration, "clientAuthentication", clientAuthentication);
    }
}
