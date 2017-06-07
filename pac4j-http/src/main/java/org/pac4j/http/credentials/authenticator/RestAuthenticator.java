package org.pac4j.http.credentials.authenticator;

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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * Authenticates against a REST API. The username/password are passed as a basic auth via a POST request,
 * the JSON response is a user profile.
 *
 * @author Jerome Leleu
 * @since 2.1.0
 */
public class RestAuthenticator extends ProfileDefinitionAware<RestProfile> implements Authenticator<UsernamePasswordCredentials> {

    private String url;

    public RestAuthenticator() {}

    public RestAuthenticator(final String url) {
        this.url = url;
    }

    @Override
    protected void internalInit(final WebContext context) {
        assertNotBlank("url", url);
        defaultProfileDefinition(new CommonProfileDefinition<>(x -> new RestProfile()));
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) throws HttpAction, CredentialsException {
        init(context);

        final String username = credentials.getUsername();
        final String password = credentials.getPassword();
        if (isBlank(username) || isBlank(password)) {
            throw new CredentialsException("empty username or password");
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
            final String body = HttpUtils.readBody(connection);

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
