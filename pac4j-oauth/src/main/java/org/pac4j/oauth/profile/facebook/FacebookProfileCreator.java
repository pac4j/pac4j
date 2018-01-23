package org.pac4j.oauth.profile.facebook;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * A specific Facebook profile creator.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class FacebookProfileCreator extends OAuth20ProfileCreator<FacebookProfile> {

    private static final String EXCHANGE_TOKEN_URL = "https://graph.facebook.com/v2.8/oauth/access_token?grant_type=fb_exchange_token";

    private static final String EXCHANGE_TOKEN_PARAMETER = "fb_exchange_token";

    public FacebookProfileCreator(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    @Override
    protected FacebookProfile retrieveUserProfileFromToken(final WebContext context, final OAuth2AccessToken accessToken) {
        final OAuth20ProfileDefinition<FacebookProfile, OAuth20Configuration> profileDefinition =
            (OAuth20ProfileDefinition<FacebookProfile, OAuth20Configuration>) configuration.getProfileDefinition();
        final FacebookConfiguration facebookConfiguration = (FacebookConfiguration) configuration;
        final String profileUrl = profileDefinition.getProfileUrl(accessToken, configuration);
        final OAuth20Service service = this.configuration.buildService(context, client, null);
        String body = sendRequestForData(service, accessToken, profileUrl, Verb.GET);
        if (body == null) {
            throw new HttpCommunicationException("Not data found for accessToken: " + accessToken);
        }
        final FacebookProfile profile = profileDefinition.extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        if (profile != null && facebookConfiguration.isRequiresExtendedToken()) {
            String url = CommonHelper.addParameter(EXCHANGE_TOKEN_URL, OAuthConstants.CLIENT_ID, configuration.getKey());
            url = CommonHelper.addParameter(url, OAuthConstants.CLIENT_SECRET, configuration.getSecret());
            url = addExchangeToken(url, accessToken);
            final OAuthRequest request = createOAuthRequest(url, Verb.GET);
            final long t0 = System.currentTimeMillis();
            final Response response;
            final int code;
            try {
                response = service.execute(request);
                body = response.getBody();
                code = response.getCode();
            } catch (final IOException | InterruptedException | ExecutionException e) {
                throw new HttpCommunicationException("Error getting body:" + e.getMessage());
            }
            final long t1 = System.currentTimeMillis();
            logger.debug("Request took: " + (t1 - t0) + " ms for: " + url);
            logger.debug("response code: {} / response body: {}", code, body);
            if (code == 200) {
                logger.debug("Retrieve extended token from  {}", body);
                final OAuth2AccessToken extendedAccessToken;
                try {
                    extendedAccessToken = ((DefaultApi20) configuration.getApi()).getAccessTokenExtractor().extract(response);
                } catch (IOException | OAuthException ex) {
                    throw new HttpCommunicationException("Error extracting token: " + ex.getMessage());
                }
                logger.debug("Extended token: {}", extendedAccessToken);
                addAccessTokenToProfile(profile, extendedAccessToken);
            } else {
                logger.error("Cannot get extended token: {} / {}", code, body);
            }
        }
        return profile;
    }

    /**
     * Adds the token to the URL in question. If we require appsecret_proof, then this method
     * will also add the appsecret_proof parameter to the URL, as Facebook expects.
     *
     * @param url the URL to modify
     * @param accessToken the token we're passing back and forth
     * @return url with additional parameter(s)
     */
    protected String addExchangeToken(final String url, final OAuth2AccessToken accessToken) {
        final FacebookProfileDefinition profileDefinition = (FacebookProfileDefinition) configuration.getProfileDefinition();
        final FacebookConfiguration facebookConfiguration = (FacebookConfiguration) configuration;
        String computedUrl = url;
        if (facebookConfiguration.isUseAppsecretProof()) {
            computedUrl = profileDefinition.computeAppSecretProof(computedUrl, accessToken, facebookConfiguration);
        }
        return CommonHelper.addParameter(computedUrl, EXCHANGE_TOKEN_PARAMETER, accessToken.getAccessToken());
    }
}
