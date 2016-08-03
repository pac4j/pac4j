package org.pac4j.oidc.credentials.extractor;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.*;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.credentials.OidcCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Extract the authorization code on the callback.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcExtractor extends InitializableWebObject implements CredentialsExtractor<OidcCredentials> {

    private static final Logger logger = LoggerFactory.getLogger(OidcExtractor.class);

    private OidcConfiguration configuration;

    private String clientName;

    public OidcExtractor() {}

    public OidcExtractor(final OidcConfiguration configuration, final String clientName) {
        this.configuration = configuration;
        this.clientName = clientName;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotBlank("clientName", clientName);
    }

    @Override
    public OidcCredentials extract(WebContext context) throws HttpAction {

        Map<String, String> parameters = toSingleParameter(context.getRequestParameters());
        AuthenticationResponse response;
        try {
            response = AuthenticationResponseParser.parse(new URI(configuration.getCallbackUrl()), parameters);
        } catch (final URISyntaxException | ParseException e) {
            throw new TechnicalException(e);
        }

        if (response instanceof AuthenticationErrorResponse) {
            logger.error("Bad authentication response, error={}",
                    ((AuthenticationErrorResponse) response).getErrorObject());
            return null;
        }

        logger.debug("Authentication response successful, get authorization code");
        AuthenticationSuccessResponse successResponse = (AuthenticationSuccessResponse) response;

        // state value must be equal
        if (!successResponse.getState().equals(context.getSessionAttribute(OidcConfiguration.STATE_ATTRIBUTE))) {
            throw new TechnicalException("State parameter is different from the one sent in authentication request. "
                    + "Session expired or possible threat of cross-site request forgery");
        }
        // Get authorization code
        final AuthorizationCode code = successResponse.getAuthorizationCode();

        return new OidcCredentials(code, clientName);
    }

    protected Map<String, String> toSingleParameter(final Map<String, String[]> requestParameters) {
        final Map<String, String> map = new HashMap<>();
        for (final Map.Entry<String, String[]> entry : requestParameters.entrySet()) {
            map.put(entry.getKey(), entry.getValue()[0]);
        }
        return map;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", configuration, "clientName", clientName);
    }
}
