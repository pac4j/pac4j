package org.pac4j.scribe.extractors;

import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.scribe.model.OrcidToken;

import java.util.regex.Pattern;

/**
 * This class represents a specific JSON extractor for ORCiD using OAuth protocol version 2. It could be part of the Scribe library.
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidJsonExtractor extends OAuth2AccessTokenJsonExtractor {

    public static final Pattern ORCID_REGEX = Pattern.compile("\"orcid\"\\s*:\\s*\"(\\S*?)\"");

    protected OrcidJsonExtractor() {
    }

    private static class InstanceHolder {

        private static final OrcidJsonExtractor INSTANCE = new OrcidJsonExtractor();
    }

    public static OrcidJsonExtractor instance() {
        return OrcidJsonExtractor.InstanceHolder.INSTANCE;
    }

    @Override
    protected OAuth2AccessToken createToken(String accessToken, String tokenType, Integer expiresIn,
                                            String refreshToken, String scope, String response) {
        return new OrcidToken(accessToken, tokenType, expiresIn, refreshToken, scope,
                extractParameter(response, ORCID_REGEX, true), response);
    }
}
