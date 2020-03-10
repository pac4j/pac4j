package org.pac4j.scribe.extractors;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.scribe.model.OrcidToken;

/**
 * This class represents a specific JSON extractor for ORCiD using OAuth protocol version 2. It could be part of the Scribe library.
 *
 * @author Jens Tinglev
 * @since 1.6.0
 */
public class OrcidJsonExtractor extends OAuth2AccessTokenJsonExtractor {

    protected OrcidJsonExtractor() {
    }

    private static class InstanceHolder {

        private static final OrcidJsonExtractor INSTANCE = new OrcidJsonExtractor();
    }

    public static OrcidJsonExtractor instance() {
        return OrcidJsonExtractor.InstanceHolder.INSTANCE;
    }

    @Override
    protected OAuth2AccessToken createToken(final String accessToken, final String tokenType,
                                            final Integer expiresIn, final String refreshToken, final String scope,
                                            final JsonNode response, final String rawResponse) {
        return new OrcidToken(accessToken, tokenType, expiresIn, refreshToken, scope,
                extractRequiredParameter(response, "orcid", rawResponse).asText(), rawResponse);
    }
}
