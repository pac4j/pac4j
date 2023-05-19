package org.pac4j.scribe.extractors;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.scribe.model.WeiboToken;

/**
 * This class represents a specific JSON extractor for Weibo using OAuth protocol version 2. It
 * could be part of the Scribe library.
 *
 * @author zhangzhenli
 * @since 3.1.0
 */
public class WeiboJsonExtractor extends OAuth2AccessTokenJsonExtractor {

    private static class InstanceHolder {
        private static final WeiboJsonExtractor INSTANCE = new WeiboJsonExtractor();
    }

    /**
     * <p>instance.</p>
     *
     * @return a {@link WeiboJsonExtractor} object
     */
    public static WeiboJsonExtractor instance() {
        return WeiboJsonExtractor.InstanceHolder.INSTANCE;
    }

    /** {@inheritDoc} */
    @Override
    protected OAuth2AccessToken createToken(String accessToken, String tokenType, Integer expiresIn, String refreshToken, String scope,
                                            JsonNode response, String rawResponse) {
        var token = super.createToken(accessToken, tokenType, expiresIn, refreshToken,
            scope, response, rawResponse);
        var uid = extractRequiredParameter(response, "uid", rawResponse).asText();
        if (uid == null || Pac4jConstants.EMPTY_STRING.equals(uid)) {
            throw new OAuthException(
                "There is no required UID in the response of the AssessToken endpoint.");
        }
        return new WeiboToken(token, uid);
    }
}
