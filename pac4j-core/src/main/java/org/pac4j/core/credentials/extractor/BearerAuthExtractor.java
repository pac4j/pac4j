package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;

/**
 * To extract an RFC 6750 bearer auth header.
 *
 * @author Graham Leggett
 * @since 4.0.1
 */
public class BearerAuthExtractor implements CredentialsExtractor<TokenCredentials> {

    private final HeaderExtractor extractor;

    public BearerAuthExtractor() {
        this(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BEARER_HEADER_PREFIX);
    }

    public BearerAuthExtractor(final String headerName, final String prefixHeader) {
        this.extractor = new HeaderExtractor(headerName, prefixHeader);
    }

    @Override
    public TokenCredentials extract(WebContext context) {
        return this.extractor.extract(context);
    }
}
