package org.pac4j.oauth.client;

import org.apache.commons.lang3.RandomStringUtils;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.BaseOAuth20Client;
import org.pac4j.oauth.client.exception.OAuthCredentialsException;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.scribe.oauth.StateOAuth20Service;

/**
 * Created by jim on 1/4/16.
 */
public abstract  class BaseOAuth20StateClient<U extends OAuth20Profile> extends BaseOAuth20Client {

    private static final String STATE_PARAMETER = "#oauth20StateParameter";

    private String stateData;

    protected String getState() {
        final String stateParameter;
        if (CommonHelper.isNotBlank(stateData)) {
            stateParameter = stateData;
        } else {
            stateParameter = RandomStringUtils.randomAlphanumeric(10);
        }
        return stateParameter;
    }


    public void setState(String stateParameter) {
        stateData = stateParameter;
    }

    protected String getAuthorizationUrl(String state) {
        final String authorizationUrl = ((StateOAuth20Service) this.service).getAuthorizationUrl(state);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        return authorizationUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String retrieveAuthorizationUrl(final WebContext context) {
        final String state = getState();
        context.setSessionAttribute(getName() + STATE_PARAMETER, state);
        return getAuthorizationUrl(state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected OAuthCredentials getOAuthCredentials(final WebContext context) {
        // check state parameter if required
        final String stateParameter = context.getRequestParameter("state");

        if (CommonHelper.isNotBlank(stateParameter)) {
            final String sessionState = (String) context.getSessionAttribute(getName() + STATE_PARAMETER);
            // clean from session after retrieving it
            context.setSessionAttribute(getName() + STATE_PARAMETER, null);
            logger.debug("sessionState : {} / stateParameter : {}", sessionState, stateParameter);
            if (!stateParameter.equals(sessionState)) {
                final String message = "State parameter mismatch : session expired or possible threat of cross-site request forgery";
                throw new OAuthCredentialsException(message);
            }
        } else {
            final String message = "Missing state parameter : session expired or possible threat of cross-site request forgery";
            throw new OAuthCredentialsException(message);
        }

        return super.getOAuthCredentials(context);
    }

}
