package org.pac4j.oauth.credentials.authenticator;

import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.OAuth10aService;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.oauth.credentials.OAuth10Credentials;

/**
 * Builds the OAuth 1 access token request, before signing it with the request token secret.
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
public class OAuth10TokenRequestBuilder implements TokenRequestBuilder<OAuth10Credentials, OAuth10aService> {

    /** {@inheritDoc} */
    @Override
    public OAuthRequest build(final CallContext ctx, final OAuth10Credentials credentials, final OAuth10aService service) {
        val api = service.getApi();
        val request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
        request.addOAuthParameter(OAuthConstants.TOKEN, credentials.getRequestToken().getToken());
        request.addOAuthParameter(OAuthConstants.VERIFIER, credentials.getVerifier());
        return request;
    }
}
