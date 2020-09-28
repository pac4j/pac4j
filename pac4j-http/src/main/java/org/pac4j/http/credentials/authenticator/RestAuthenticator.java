package org.pac4j.http.credentials.authenticator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpUtils;
import org.pac4j.http.profile.RestProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Authenticates against a REST API. The username/password are passed as a basic auth via a POST request,
 * the JSON response is a user profile.
 *
 * @author Jerome Leleu
 * @since 2.1.0
 */
public class RestAuthenticator extends ProfileDefinitionAware<RestProfile> implements Authenticator {

    private static final Logger logger = LoggerFactory.getLogger(RestAuthenticator.class);

    private ObjectMapper mapper;

    private String url;

    public RestAuthenticator() {}

    public RestAuthenticator(final String url) {
        this.url = url;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotBlank("url", url);
        defaultProfileDefinition(new CommonProfileDefinition<>(x -> new RestProfile()));
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }

    @Override
    public void validate(final Credentials cred, final WebContext context) {
        init();

        final UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) cred;
        final String username = credentials.getUsername();
        final String password = credentials.getPassword();
        if (CommonHelper.isBlank(username) || CommonHelper.isBlank(password)) {
            logger.info("Empty username or password");
            return;
        }

        final String body = callRestApi(username, password);
        logger.debug("body: {}", body);
        if (body != null) {
            buildProfile(credentials, body);
        }
    }

    protected void buildProfile(final UsernamePasswordCredentials credentials, final String body) {
        final RestProfile profileClass = getProfileDefinition().newProfile();
        final RestProfile profile;
        try {
            profile = mapper.readValue(body, profileClass.getClass());
        } catch (final IOException e) {
            throw new TechnicalException(e);
        }
        logger.debug("profile: {}", profile);
        credentials.setUserProfile(profile);
    }

    /**
     * Return the body from the REST API, passing the username/pasword auth.
     * To be overridden using another HTTP client if necessary.
     *
     * @param username the username
     * @param password the password
     * @return the response body
     */
    protected String callRestApi(final String username, final String password) {

        final String basicAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        final Map<String, String> headers = new HashMap<>();
        headers.put(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BASIC_HEADER_PREFIX + basicAuth);

        HttpURLConnection connection = null;
        try {
            connection = HttpUtils.openPostConnection(new URL(url), headers);
            int code = connection.getResponseCode();
            if (code == 200) {
                logger.debug("Authentication success for username: {}", username);
                return HttpUtils.readBody(connection);
            } else if (code == 401 || code == 403) {
                logger.info("Authentication failure for username: {} -> {}", username, HttpUtils.buildHttpErrorMessage(connection));
                return null;
            } else {
                logger.warn("Unexpected error for username: {} -> {}", username, HttpUtils.buildHttpErrorMessage(connection));
                return null;
            }
        } catch (final IOException e) {
            throw new TechnicalException(e);
        } finally {
            HttpUtils.closeConnection(connection);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "url", url, "mapper", mapper);
    }
}
