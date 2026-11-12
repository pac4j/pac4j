package org.pac4j.oauth.credentials.authenticator;

import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import org.pac4j.scribe.service.TokenRequestFactory;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.oauth.credentials.OAuth20Credentials;

/**
 * Builds the standard OAuth 2 authorization code token request using the provider's authentication scheme.
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
public class OAuth20TokenRequestBuilder implements TokenRequestBuilder<OAuth20Credentials, OAuth20Service> {

    /** {@inheritDoc} */
    @Override
    public OAuthRequest build(final CallContext ctx, final OAuth20Credentials credentials, final OAuth20Service service) {
        if (service instanceof TokenRequestFactory factory) {
            return factory.createAccessTokenRequest(AccessTokenRequestParams.create(credentials.getCode()));
        }
        val api = service.getApi();
        val request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
        api.getClientAuthentication().addClientAuthentication(request, service.getApiKey(), service.getApiSecret());
        request.addParameter(OAuthConstants.CODE, credentials.getCode());
        if (service.getCallback() != null) {
            request.addParameter(OAuthConstants.REDIRECT_URI, service.getCallback());
        }
        if (service.getDefaultScope() != null) {
            request.addParameter(OAuthConstants.SCOPE, service.getDefaultScope());
        }
        request.addParameter(OAuthConstants.GRANT_TYPE, OAuthConstants.AUTHORIZATION_CODE);
        return request;
    }
}
