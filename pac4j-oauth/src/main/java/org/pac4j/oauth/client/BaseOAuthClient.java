package org.pac4j.oauth.client;

import com.github.scribejava.core.builder.api.Api;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuthService;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.RedirectAction;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.OAuth10Profile;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a base implementation for an OAuth protocol client based on the Scribe library. It should work for all OAuth clients. In
 * subclasses, some methods are to be implemented / customized for specific needs depending on the client.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public abstract class BaseOAuthClient<U extends OAuth20Profile> extends IndirectClient<OAuthCredentials, U> {

    protected static final Logger logger = LoggerFactory.getLogger(BaseOAuthClient.class);

    protected OAuthService service;

    private String key;

    private String secret;

    private boolean tokenAsHeader = false;

    private int connectTimeout = HttpConstants.DEFAULT_CONNECT_TIMEOUT;

    private int readTimeout = HttpConstants.DEFAULT_READ_TIMEOUT;

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("key", this.key);
        CommonHelper.assertNotBlank("secret", this.secret);
        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);

        this.service = getApi().createService(buildOAuthConfig(context));
    }

    /**
     * Build an OAuth configuration.
     *
     * @param context the web context
     * @return the OAuth configuration
     */
    protected OAuthConfig buildOAuthConfig(final WebContext context) {
        return new OAuthConfig(this.key, this.secret, computeFinalCallbackUrl(context),
                SignatureType.Header, getOAuthScope(), null, this.connectTimeout, this.readTimeout, hasOAuthGrantType() ? "authorization_code" : null);
    }

    /**
     * Define the OAuth API for this client.
     *
     * @return the OAuth API
     */
    protected abstract Api getApi();

    /**
     * Define the OAuth scope for this client.
     *
     * @return the OAuth scope
     */
    protected String getOAuthScope() {
        return null;
    }


    /**
     * Whether the grant type must be added.
     *
     * @return Whether the grant type must be added
     */
    protected boolean hasOAuthGrantType() {
        return false;
    }

    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext context) {
        try {
            return RedirectAction.redirect(retrieveAuthorizationUrl(context));
        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Retrieve the authorization url to redirect to the OAuth provider.
     *
     * @param context the web context
     * @return the authorization url
     */
    protected abstract String retrieveAuthorizationUrl(final WebContext context);

    @Override
    protected OAuthCredentials retrieveCredentials(final WebContext context) {
        // check if the authentication has been cancelled
        if (hasBeenCancelled(context)) {
            logger.debug("authentication has been cancelled by user");
            return null;
        }
        // check errors
        try {
            boolean errorFound = false;
            final OAuthCredentialsException oauthCredentialsException = new OAuthCredentialsException("Failed to retrieve OAuth credentials, error parameters found");
            for (final String key : OAuthCredentialsException.ERROR_NAMES) {
                final String value = context.getRequestParameter(key);
                if (value != null) {
                    errorFound = true;
                    oauthCredentialsException.setErrorMessage(key, value);
                }
            }
            if (errorFound) {
                throw oauthCredentialsException;
            } else {
                return getOAuthCredentials(context);
            }
        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Return if the authentication has been cancelled.
     * 
     * @param context the web context.
     * @return if the authentication has been cancelled.
     */
    protected boolean hasBeenCancelled(WebContext context) {
        return false;
    }

    /**
     * Get the OAuth credentials from the web context.
     * 
     * @param context the web context
     * @return the OAuth credentials
     */
    protected abstract OAuthCredentials getOAuthCredentials(final WebContext context);

    @Override
    protected U retrieveUserProfile(final OAuthCredentials credentials, final WebContext context) {
        try {
            final Token token = getAccessToken(credentials);
            return retrieveUserProfileFromToken(token);
        } catch (final OAuthException e) {
            throw new TechnicalException(e);
        }
    }

    /**
     * Get the user profile from the access token.
     *
     * @param context the web context
     * @param accessToken the access token
     * @return the user profile
     */
    public U getUserProfile(final WebContext context, final String accessToken) {
        init(context);
        try {
            final Token token = new Token(accessToken, "");
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
     */
    protected abstract Token getAccessToken(OAuthCredentials credentials);

    /**
     * Retrieve the user profile from the access token.
     * 
     * @param accessToken the access token
     * @return the user profile
     */
    protected U retrieveUserProfileFromToken(final Token accessToken) {
        final String body = sendRequestForData(accessToken, getProfileUrl(accessToken));
        if (body == null) {
            throw new HttpCommunicationException("Not data found for accessToken: " + accessToken);
        }
        final U profile = extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        return profile;
    }

    /**
     * Retrieve the url of the profile of the authenticated user for the provider.
     *
     * @param accessToken only used when constructing dynamic urls from data in the token
     * @return the url of the user profile given by the provider
     */
    protected abstract String getProfileUrl(final Token accessToken);

    /**
     * Make a request to get the data of the authenticated user for the provider.
     * 
     * @param accessToken the access token
     * @param dataUrl url of the data
     * @return the user data response
     */
    protected String sendRequestForData(final Token accessToken, final String dataUrl) {
        logger.debug("accessToken: {} / dataUrl: {}", accessToken, dataUrl);
        final long t0 = System.currentTimeMillis();
        final OAuthRequest request = createOAuthRequest(dataUrl);
        this.service.signRequest(accessToken, request);
        // Let the client to decide if the token should be in header
        if (this.isTokenAsHeader()) {
            request.addHeader("Authorization", "Bearer " + accessToken.getToken());
        }
        final Response response = request.send();
        final int code = response.getCode();
        final String body = response.getBody();
        final long t1 = System.currentTimeMillis();
        logger.debug("Request took: " + (t1 - t0) + " ms for: " + dataUrl);
        logger.debug("response code: {} / response body: {}", code, body);
        if (code != 200) {
            throw new HttpCommunicationException(code, body);
        }
        return body;
    }

    /**
     * Create an OAuth request.
     *
     * @param url the url to call
     * @return the request
     */
    protected OAuthRequest createOAuthRequest(final String url) {
        return new OAuthRequest(Verb.GET, url, this.service);
    }

    /**
     * Make a request to the OAuth provider to access a protected resource. The profile should contain a valid access token (and secret if
     * needed).
     * 
     * @param profile user profile
     * @param dataUrl url of the data
     * @return the body of the requested resource
     */
    public String sendRequestForData(final OAuth10Profile profile, final String dataUrl) {
        final String secret = profile.getAccessSecret();
        final Token accessToken = new Token(profile.getAccessToken(), secret == null ? "" : secret);
        return sendRequestForData(accessToken, dataUrl);
    }

    /**
     * Extract the user profile from the response (JSON, XML...) of the profile url.
     * 
     * @param body the response body
     * @return the user profile object
     */
    protected abstract U extractUserProfile(String body);

    /**
     * Add the access token to the profile (as an attribute).
     * 
     * @param profile the user profile
     * @param accessToken the access token
     */
    protected void addAccessTokenToProfile(final U profile, final Token accessToken) {
        if (profile != null) {
            final String token = accessToken.getToken();
            logger.debug("add access_token: {} to profile", token);
            profile.setAccessToken(token);
        }
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public String getKey() {
        return this.key;
    }

    public String getSecret() {
        return this.secret;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public boolean isTokenAsHeader() {
        return tokenAsHeader;
    }

    public void setTokenAsHeader(boolean tokenAsHeader) {
        this.tokenAsHeader = tokenAsHeader;
    }
}
