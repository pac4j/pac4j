package org.pac4j.http.credentials.authenticator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpUtils;
import org.pac4j.http.profile.RestProfile;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Authenticates against a REST API. The username/password are passed as a basic auth via a POST request,
 * the JSON response is a user profile.
 *
 * @author Jerome Leleu
 * @since 2.1.0
 */
@Getter
@Setter
@ToString
@Slf4j
public class RestAuthenticator extends ProfileDefinitionAware implements Authenticator {

    private ObjectMapper mapper;

    private String url;

    /**
     * <p>Constructor for RestAuthenticator.</p>
     */
    public RestAuthenticator() {}

    /**
     * <p>Constructor for RestAuthenticator.</p>
     *
     * @param url a {@link String} object
     */
    public RestAuthenticator(final String url) {
        this.url = url;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotBlank("url", url);
        setProfileDefinitionIfUndefined(new CommonProfileDefinition(x -> new RestProfile()));
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials cred) {
        init();

        val credentials = (UsernamePasswordCredentials) cred;
        val username = credentials.getUsername();
        val password = credentials.getPassword();
        if (CommonHelper.isBlank(username) || CommonHelper.isBlank(password)) {
            LOGGER.info("Empty username or password");
            return Optional.of(credentials);
        }

        val body = callRestApi(username, password);
        LOGGER.debug("body: {}", body);
        if (body != null) {
            buildProfile(credentials, body);
        }

        return Optional.of(credentials);
    }

    /**
     * <p>buildProfile.</p>
     *
     * @param credentials a {@link UsernamePasswordCredentials} object
     * @param body a {@link String} object
     */
    protected void buildProfile(final UsernamePasswordCredentials credentials, final String body) {
        val profileClass = (RestProfile) getProfileDefinition().newProfile();
        final RestProfile profile;
        try {
            profile = mapper.readValue(body, profileClass.getClass());
        } catch (final IOException e) {
            throw new TechnicalException(e);
        }
        LOGGER.debug("profile: {}", profile);
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

        val basicAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BASIC_HEADER_PREFIX + basicAuth);

        HttpURLConnection connection = null;
        try {
            connection = HttpUtils.openPostConnection(new URL(url), headers);
            val code = connection.getResponseCode();
            if (code == 200) {
                LOGGER.debug("Authentication success for username: {}", username);
                return HttpUtils.readBody(connection);
            } else if (code == 401 || code == 403) {
                LOGGER.info("Authentication failure for username: {} -> {}", username, HttpUtils.buildHttpErrorMessage(connection));
                return null;
            } else {
                LOGGER.warn("Unexpected error for username: {} -> {}", username, HttpUtils.buildHttpErrorMessage(connection));
                return null;
            }
        } catch (final IOException e) {
            throw new TechnicalException(e);
        } finally {
            HttpUtils.closeConnection(connection);
        }
    }
}
