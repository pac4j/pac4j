package org.pac4j.oauth.client;

import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.SignatureType;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * This class is the base implementation for client supporting OAuth protocol version 2.0 with the state parameter.
 *
 * @author James Kleeh
 * @since 1.8.4
 */
public abstract class BaseOAuth20StateClient<U extends OAuth20Profile> extends BaseOAuth20Client<U> {

    private static final String STATE_PARAMETER = "#oauth20StateParameter";

    private String stateData;

    @Override
    protected String getStateParameter(final WebContext context) {
        final String stateParameter;
        if (CommonHelper.isNotBlank(stateData)) {
            stateParameter = stateData;
        } else {
            stateParameter = CommonHelper.randomString(10);
        }
        return stateParameter;
    }

    @Override
    protected OAuthConfig buildOAuthConfig(WebContext context) {
        final String state = getStateParameter(context);
        logger.debug("save sessionState: {}", state);
        // the state is held in a specific context.
        context.setSessionAttribute(getName() + STATE_PARAMETER, state);
        return new OAuthConfig(this.getKey(), this.getSecret(), computeFinalCallbackUrl(context),
                SignatureType.Header, getOAuthScope(), null, this.getConnectTimeout(), this.getReadTimeout(), hasOAuthGrantType() ? "authorization_code" : null, state, this.getResponseType());
    }

    @Override
    protected String retrieveAuthorizationUrl(final WebContext context) throws HttpAction {
        // create a specific configuration with state
        final OAuthConfig config = buildOAuthConfig(context);

        // create a specific service
        final OAuth20Service newService = getApi().createService(config);
        final String authorizationUrl = newService.getAuthorizationUrl();
        logger.debug("authorizationUrl: {}", authorizationUrl);
        return authorizationUrl;
    }

    @Override
    protected OAuthCredentials getOAuthCredentials(final WebContext context) throws HttpAction {
        // check state parameter if required
        final String stateParameter = context.getRequestParameter("state");

        if (CommonHelper.isNotBlank(stateParameter)) {
            final String sessionState = (String) context.getSessionAttribute(getName() + STATE_PARAMETER);
            // clean from session after retrieving it
            context.setSessionAttribute(getName() + STATE_PARAMETER, null);
            logger.debug("sessionState: {} / stateParameter: {}", sessionState, stateParameter);
            if (!stateParameter.equals(sessionState)) {
                final String message = "State parameter mismatch: session expired or possible threat of cross-site request forgery";
                throw new OAuthCredentialsException(message);
            }
        } else {
            final String message = "Missing state parameter: session expired or possible threat of cross-site request forgery";
            throw new OAuthCredentialsException(message);
        }

        return super.getOAuthCredentials(context);
    }

    public String getStateData() {
        return stateData;
    }

    public void setStateData(String stateData) {
        this.stateData = stateData;
    }
}
