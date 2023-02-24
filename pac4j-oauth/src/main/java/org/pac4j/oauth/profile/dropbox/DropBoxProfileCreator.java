package org.pac4j.oauth.profile.dropbox;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuthService;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;

/**
 * DropBox profile creator.
 *
 * @author Matthias Bohlen
 * @since 3.0.0
 */
public class DropBoxProfileCreator extends OAuth20ProfileCreator {

    /**
     * <p>Constructor for DropBoxProfileCreator.</p>
     *
     * @param configuration a {@link org.pac4j.oauth.config.OAuth20Configuration} object
     * @param client a {@link org.pac4j.core.client.IndirectClient} object
     */
    public DropBoxProfileCreator(final OAuth20Configuration configuration, final IndirectClient client) {
        super(configuration, client);
    }

    /** {@inheritDoc} */
    @Override
    protected void signRequest(final OAuthService service, final Token accessToken, final OAuthRequest request) {
        request.addHeader(HttpConstants.CONTENT_TYPE_HEADER, HttpConstants.APPLICATION_JSON);
        request.addHeader(HttpConstants.AUTHORIZATION_HEADER,
            HttpConstants.BEARER_HEADER_PREFIX + ((OAuth2AccessToken) accessToken).getAccessToken());
        request.setPayload("null");
    }
}
