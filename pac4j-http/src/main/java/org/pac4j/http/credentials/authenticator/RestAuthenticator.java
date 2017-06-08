package org.pac4j.http.credentials.authenticator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpUtils;
import org.pac4j.http.profile.RestProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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
public class RestAuthenticator extends ProfileDefinitionAware<RestProfile> implements Authenticator<UsernamePasswordCredentials> {

    private static final Logger logger = LoggerFactory.getLogger(RestAuthenticator.class);

    private static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    private String url;

    public RestAuthenticator() {}

    public RestAuthenticator(final String url) {
        this.url = url;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("url", url);
        defaultProfileDefinition(new CommonProfileDefinition<>(x -> new RestProfile()));
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) throws HttpAction, CredentialsException {
        init(context);

        final String username = credentials.getUsername();
        final String password = credentials.getPassword();
        if (CommonHelper.isBlank(username) || CommonHelper.isBlank(password)) {
            logger.error("Empty username or password");
            return;
        }

        final String basicAuth;
        try {
            basicAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
        } catch (final UnsupportedEncodingException e) {
            throw new TechnicalException(e);
        }

        final Map<String, String> headers = new HashMap<>();
        headers.put(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BASIC_HEADER_PREFIX + basicAuth);

        HttpURLConnection connection = null;
        try {
            connection = HttpUtils.openPostConnection(new URL(url), headers);
            int code = connection.getResponseCode();
            if (code != 200) {
                logger.error("Failed response: {}", HttpUtils.buildHttpErrorMessage(connection));
            } else {
                final String body = HttpUtils.readBody(connection);
                logger.debug("body: {}", body);
                final RestProfile profileClass = getProfileDefinition().newProfile();
                final RestProfile profile = mapper.readValue(body, profileClass.getClass());
                logger.debug("profile: {}", profile);
                credentials.setUserProfile(profile);
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

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "url", url);
    }
}
