package org.pac4j.oauth.profile.creator;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuthService;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * OAuth profile creator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
abstract class OAuthProfileCreator<C extends OAuthCredentials, U extends CommonProfile, O extends OAuthConfiguration<S, T>,
    T extends Token, S extends OAuthService> implements ProfileCreator<C> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected O configuration;

    protected IndirectClient client;

    protected OAuthProfileCreator(final O configuration, final IndirectClient client) {
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    public Optional<UserProfile> create(final C credentials, final WebContext context) {
        try {
            final T token = getAccessToken(credentials);
            return retrieveUserProfileFromToken(context, token);
        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Get the access token from OAuth credentials.
     *
     * @param credentials credentials
     * @return the access token
     */
    protected abstract T getAccessToken(final C credentials);

    /**
     * Retrieve the user profile from the access token.
     *
     * @param context the web context
     * @param accessToken the access token
     * @return the user profile
     */
    protected Optional<UserProfile> retrieveUserProfileFromToken(final WebContext context, final T accessToken) {
        final OAuthProfileDefinition<U, T, O> profileDefinition = configuration.getProfileDefinition();
        final String profileUrl = profileDefinition.getProfileUrl(accessToken, configuration);
        final S service = this.configuration.buildService(context, client, null);
        final String body = sendRequestForData(service, accessToken, profileUrl, profileDefinition.getProfileVerb());
        logger.info("UserProfile: " + body);
        if (body == null) {
            throw new HttpCommunicationException("No data found for accessToken: " + accessToken);
        }
        final U profile = (U) configuration.getProfileDefinition().extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        return Optional.of(profile);
    }

    /**
     * Make a request to get the data of the authenticated user for the provider.
     *
     * @param service the OAuth service
     * @param accessToken the access token
     * @param dataUrl     url of the data
     * @param verb        method used to request data
     * @return the user data response
     */
    protected String sendRequestForData(final S service, final T accessToken, final String dataUrl, Verb verb) {
        logger.debug("accessToken: {} / dataUrl: {}", accessToken, dataUrl);
        final long t0 = System.currentTimeMillis();
        final OAuthRequest request = createOAuthRequest(dataUrl, verb);
        signRequest(service, accessToken, request);
        final String body;
        final int code;
        try {
            Response response = service.execute(request);
            code = response.getCode();
            body = response.getBody();
        } catch (final IOException | InterruptedException | ExecutionException e) {
            throw new HttpCommunicationException("Error getting body: " + e.getMessage());
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
     * @param service the service
     * @param token the token
     * @param request the request
     */
    protected abstract void signRequest(S service, T token, OAuthRequest request);

    /**
     * Create an OAuth request.
     *
     * @param url the url to call
     * @param verb method used to create the request
     * @return the request
     */
    protected OAuthRequest createOAuthRequest(final String url, final Verb verb) {
        return new OAuthRequest(verb, url);
    }

    /**
     * Add the access token to the profile (as an attribute).
     *
     * @param profile     the user profile
     * @param accessToken the access token
     */
    protected abstract void addAccessTokenToProfile(final U profile, final T accessToken);
}
