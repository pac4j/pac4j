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

    /** Constant <code>BASIC_AUTH_AUTHENTICATION_METHOD="basicAuth"</code> */
    public static final String BASIC_AUTH_AUTHENTICATION_METHOD = "basicAuth";

    /** Constant <code>REQUEST_BODY_AUTHENTICATION_METHOD="requestBody"</code> */
    public static final String REQUEST_BODY_AUTHENTICATION_METHOD = "requestBody";

    protected final String authUrl;
    protected final String tokenUrl;
    protected Verb accessTokenVerb = Verb.POST;
    protected String clientAuthenticationMethod = BASIC_AUTH_AUTHENTICATION_METHOD;

    /**
     * <p>Constructor for GenericApi20.</p>
     *
     * @param authUrl a {@link String} object
     * @param tokenUrl a {@link String} object
     */
    public GenericApi20(final String authUrl, final String tokenUrl) {
        this.authUrl = authUrl;
        this.tokenUrl = tokenUrl;
    }

    /** {@inheritDoc} */
    @Override
    public Verb getAccessTokenVerb() {
        return accessTokenVerb;
    }

    /**
     * <p>Setter for the field <code>accessTokenVerb</code>.</p>
     *
     * @param verb a {@link Verb} object
     */
    public void setAccessTokenVerb(final Verb verb) {
        accessTokenVerb = verb;
    }

    /** {@inheritDoc} */
    @Override
    public String getAccessTokenEndpoint() {
        return tokenUrl;
    }

    /** {@inheritDoc} */
    @Override
    protected String getAuthorizationBaseUrl() {
        return authUrl;
    }

    /**
     * <p>Getter for the field <code>clientAuthenticationMethod</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getClientAuthenticationMethod() {
        return clientAuthenticationMethod;
    }

    /**
     * <p>Setter for the field <code>clientAuthenticationMethod</code>.</p>
     *
     * @param clientAuthenticationMethod a {@link String} object
     */
    public void setClientAuthenticationMethod(final String clientAuthenticationMethod) {
        this.clientAuthenticationMethod = clientAuthenticationMethod;
    }

    /** {@inheritDoc} */
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
