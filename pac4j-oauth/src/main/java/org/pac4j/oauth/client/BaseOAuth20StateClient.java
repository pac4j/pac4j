package org.pac4j.oauth.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
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

    @Override
    protected String getStateParameter(final WebContext context) {
        final String stateParameter;
        final String stateData = getState();
        if (CommonHelper.isNotBlank(stateData)) {
            stateParameter = stateData;
        } else {
            stateParameter = CommonHelper.randomString(10);
        }
        return stateParameter;
    }

    @Override
    protected void internalInit(WebContext context) {
        // create a specific configuration with state
        this.setState(getStateParameter(context));
        CommonHelper.assertNotNull("state", this.getState());
        // save state
        context.setSessionAttribute(getName() + STATE_PARAMETER, this.getState());
        super.internalInit(context);
    }

    @Override
    protected OAuthCredentials getOAuthCredentials(final WebContext context) throws RequiresHttpAction {
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

}
