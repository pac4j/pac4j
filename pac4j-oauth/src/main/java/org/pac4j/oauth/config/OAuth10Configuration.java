package org.pac4j.oauth.config;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.oauth.OAuthService;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.pac4j.oauth.credentials.OAuth10Credentials;
import org.pac4j.oauth.credentials.authenticator.TokenRequestBuilder;
import org.pac4j.oauth.credentials.authenticator.OAuth10TokenRequestBuilder;
import lombok.AllArgsConstructor;
import lombok.With;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.oauth.util.OAuthProtocolMessages;

/**
 * The OAuh 1.0 configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@With
@AllArgsConstructor
public class OAuth10Configuration extends OAuthConfiguration {

    private TokenRequestBuilder<OAuth10Credentials, OAuth10aService> tokenRequestBuilder;

    /** Constant <code>OAUTH_VERIFIER="oauth_verifier"</code> */
    public static final String OAUTH_VERIFIER = "oauth_verifier";

    /** Constant <code>REQUEST_TOKEN="requestToken"</code> */
    public static final String REQUEST_TOKEN = "requestToken";

    /**
     * {@inheritDoc}
     *
     * Build an OAuth service from the call context.
     */
    @Override
    public OAuthService buildService(final CallContext context, final IndirectClient client) {
        init();

        val finalCallbackUrl = client.computeFinalCallbackUrl(context.webContext());

        return ((DefaultApi10a) api)
            .createService(this.key, this.secret, finalCallbackUrl, this.scope, null, null, this.httpClientConfig,
                OAuthProtocolMessages.httpClient(this.httpClientConfig));
    }

    /**
     * Return the name of the attribute storing in session the request token.
     *
     * @param clientName the client name
     * @return the name of the attribute storing in session the request token
     */
    public String getRequestTokenSessionAttributeName(final String clientName) {
        return clientName + "#" + REQUEST_TOKEN;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        super.internalInit(forceReinit);
        setTokenRequestBuilderIfNotDefined(new OAuth10TokenRequestBuilder());
    }

    /**
     * Set the default token request builder without replacing a custom one.
     *
     * @param builder the default builder
     */
    public void setTokenRequestBuilderIfNotDefined(final TokenRequestBuilder<OAuth10Credentials, OAuth10aService> builder) {
        if (tokenRequestBuilder == null) {
            tokenRequestBuilder = builder;
        }
    }
}
