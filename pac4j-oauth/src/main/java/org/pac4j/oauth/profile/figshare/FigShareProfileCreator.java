package org.pac4j.oauth.profile.figshare;

import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuthService;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;

/**
 * A specific FigShare profile creator.
 *
 * @author Vassilis Virvilis
 * @since 3.8.0
 */
public class FigShareProfileCreator extends OAuth20ProfileCreator {
    /**
     * <p>Constructor for FigShareProfileCreator.</p>
     *
     * @param configuration a {@link org.pac4j.oauth.config.OAuth20Configuration} object
     * @param client a {@link org.pac4j.core.client.IndirectClient} object
     */
    public FigShareProfileCreator(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    /** {@inheritDoc} */
    @Override
    protected void signRequest(OAuthService service, Token accessToken, OAuthRequest request) {
        super.signRequest(service, accessToken, request);
        request.addParameter(OAuthConstants.ACCESS_TOKEN, ((OAuth2AccessToken) accessToken).getAccessToken());
    }
}
