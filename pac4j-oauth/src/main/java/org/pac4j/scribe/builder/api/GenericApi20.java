package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth2.clientauthentication.ClientAuthentication;
import com.github.scribejava.core.oauth2.clientauthentication.HttpBasicAuthenticationScheme;
import com.github.scribejava.core.oauth2.clientauthentication.RequestBodyAuthenticationScheme;
import org.pac4j.core.exception.TechnicalException;

/**
 * OAuth API class for the GenericOAuth20Client
 *
 * @author aherrick
 * @since 1.9.2
 */
public class GenericApi20 extends DefaultApi20 {

    public static final String BASIC_AUTH_AUTHENTICATION_METHOD = "basicAuth";

    public static final String REQUEST_BODY_AUTHENTICATION_METHOD = "requestBody";

    protected final String authUrl;
    protected final String tokenUrl;
    protected Verb accessTokenVerb = Verb.POST;
    protected String clientAuthenticationMethod = BASIC_AUTH_AUTHENTICATION_METHOD;

    public GenericApi20(final String authUrl, final String tokenUrl) {
        this.authUrl = authUrl;
        this.tokenUrl = tokenUrl;
    }

    @Override
    public Verb getAccessTokenVerb() {
        return accessTokenVerb;
    }

    public void setAccessTokenVerb(final Verb verb) {
        accessTokenVerb = verb;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return tokenUrl;
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return authUrl;
    }

    public String getClientAuthenticationMethod() {
        return clientAuthenticationMethod;
    }

    public void setClientAuthenticationMethod(final String clientAuthenticationMethod) {
        this.clientAuthenticationMethod = clientAuthenticationMethod;
    }

    @Override
    public ClientAuthentication getClientAuthentication() {
        if (BASIC_AUTH_AUTHENTICATION_METHOD.equalsIgnoreCase(clientAuthenticationMethod)) {
            return HttpBasicAuthenticationScheme.instance();
        } else if (REQUEST_BODY_AUTHENTICATION_METHOD.equalsIgnoreCase(clientAuthenticationMethod)) {
            return RequestBodyAuthenticationScheme.instance();
        } else {
            throw new TechnicalException("Unsupported client authentication method: " + clientAuthenticationMethod);
        }
    }
}
