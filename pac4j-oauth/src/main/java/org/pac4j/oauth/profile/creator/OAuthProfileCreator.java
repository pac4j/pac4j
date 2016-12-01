package org.pac4j.oauth.profile.creator;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * OAuth profile creator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
abstract class OAuthProfileCreator<C extends OAuthCredentials, U extends CommonProfile, O extends OAuthConfiguration, T extends Token> extends InitializableWebObject implements ProfileCreator<C, U> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected final O configuration;

    protected OAuthProfileCreator(final O configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", this.configuration);
        configuration.init(context);
    }

    @Override
    public U create(final C credentials, final WebContext context) throws HttpAction {
        try {
            final T token = getAccessToken(credentials);
            return retrieveUserProfileFromToken(token);
        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Get the access token from OAuth credentials.
     *
     * @param credentials credentials
     * @return the access token
     * @throws HttpAction whether an additional HTTP action is required
     */
    protected abstract T getAccessToken(final C credentials) throws HttpAction;

    /**
     * Retrieve the user profile from the access token.
     *
     * @param accessToken the access token
     * @return the user profile
     * @throws HttpAction whether an additional HTTP action is required
     */
    protected U retrieveUserProfileFromToken(final T accessToken) throws HttpAction {
        final OAuthProfileDefinition<U, T, O> profileDefinition = configuration.getProfileDefinition();
        final String profileUrl = profileDefinition.getProfileUrl(accessToken, configuration);
        final String body = sendRequestForData(accessToken, profileUrl, profileDefinition.getProfileVerb());
        logger.info("UserProfile: " + body);
        if (body == null) {
            throw new HttpCommunicationException("No data found for accessToken: " + accessToken);
        }
        final U profile = (U) configuration.getProfileDefinition().extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        return profile;
    }

    /**
     * Make a request to get the data of the authenticated user for the provider.
     *
     * @param accessToken the access token
     * @param dataUrl     url of the data
     * @param verb        method used to request data
     * @return the user data response
     */
    protected String sendRequestForData(final T accessToken, final String dataUrl, Verb verb) {
        logger.debug("accessToken: {} / dataUrl: {}", accessToken, dataUrl);
        final long t0 = System.currentTimeMillis();
        final OAuthRequest request = createOAuthRequest(dataUrl, verb);
        signRequest(accessToken, request);
        final Response response = request.send();
        final int code = response.getCode();
        final String body;
        try {
            body = response.getBody();
        } catch (final IOException ex) {
            throw new HttpCommunicationException("Error getting body: " + ex.getMessage());
        }
        final long t1 = System.currentTimeMillis();
        logger.debug("Request took: " + (t1 - t0) + " ms for: " + dataUrl);
        logger.debug("response code: {} / response body: {}", code, body);
        if (code != 200) {
            throw new HttpCommunicationException(code, body);
        }
        return body;
    }

    /**
     * Sign the request.
     *
     * @param token the token
     * @param request the request
     */
    protected abstract void signRequest(final T token, final OAuthRequest request);

    /**
     * Create an OAuth request.
     *
     * @param url the url to call
     * @param verb method used to create the request
     * @return the request
     */
    protected OAuthRequest createOAuthRequest(final String url, final Verb verb) {
        return new OAuthRequest(verb, url, this.configuration.getService());
    }

    /**
     * Add the access token to the profile (as an attribute).
     *
     * @param profile     the user profile
     * @param accessToken the access token
     */
    protected abstract void addAccessTokenToProfile(final U profile, final T accessToken);

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", this.configuration);
    }
}
