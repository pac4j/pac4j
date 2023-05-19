package org.pac4j.oauth.profile.creator;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuthService;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuthConfiguration;
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
abstract class OAuthProfileCreator implements ProfileCreator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Constant <code>mapper</code> */
    protected static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    protected OAuthConfiguration configuration;

    protected IndirectClient client;

    /**
     * <p>Constructor for OAuthProfileCreator.</p>
     *
     * @param configuration a {@link OAuthConfiguration} object
     * @param client a {@link IndirectClient} object
     */
    protected OAuthProfileCreator(final OAuthConfiguration configuration, final IndirectClient client) {
        CommonHelper.assertNotNull("client", client);
        CommonHelper.assertNotNull("configuration", configuration);
        this.configuration = configuration;
        this.client = client;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<UserProfile> create(final CallContext ctx, final Credentials credentials) {
        try {
            val token = getAccessToken(credentials);
            return retrieveUserProfileFromToken(ctx.webContext(), token);
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
    protected abstract Token getAccessToken(final Credentials credentials);

    /**
     * Retrieve the user profile from the access token.
     *
     * @param context the web context
     * @param accessToken the access token
     * @return the user profile
     */
    protected Optional<UserProfile> retrieveUserProfileFromToken(final WebContext context, final Token accessToken) {
        val profileDefinition = configuration.getProfileDefinition();
        val profileUrl = profileDefinition.getProfileUrl(accessToken, configuration);
        val service = this.configuration.buildService(context, client);
        val body = sendRequestForData(service, accessToken, profileUrl, profileDefinition.getProfileVerb());
        logger.info("UserProfile: " + body);
        if (body == null) {
            throw new HttpCommunicationException("No data found for accessToken: " + accessToken);
        }
        final UserProfile profile = configuration.getProfileDefinition().extractUserProfile(body);
        addTokenToProfile(profile, accessToken);
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
    protected String sendRequestForData(final OAuthService service, final Token accessToken, final String dataUrl, Verb verb) {
        logger.debug("accessToken: {} / dataUrl: {}", accessToken, dataUrl);
        val t0 = System.currentTimeMillis();
        val request = createOAuthRequest(dataUrl, verb);
        signRequest(service, accessToken, request);
        final String body;
        final int code;
        try {
            var response = service.execute(request);
            code = response.getCode();
            body = response.getBody();
        } catch (final IOException | InterruptedException | ExecutionException e) {
            throw new HttpCommunicationException("Error getting body: " + e.getMessage());
        }
        val t1 = System.currentTimeMillis();
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
    protected abstract void signRequest(OAuthService service, Token token, OAuthRequest request);

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
    protected abstract void addTokenToProfile(final UserProfile profile, final Token accessToken);
}
