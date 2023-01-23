package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;

import java.util.Optional;

/**
 * To extract an RFC 6750 bearer auth header.
 *
 * @author Graham Leggett
 * @since 3.5.0
 */
public class BearerAuthExtractor implements CredentialsExtractor {

    private final HeaderExtractor extractor;

    public BearerAuthExtractor() {
        this(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BEARER_HEADER_PREFIX);
    }

    public BearerAuthExtractor(final String headerName, final String prefixHeader) {
        this.extractor = new HeaderExtractor(headerName, prefixHeader);
    }

    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        return this.extractor.extract(ctx);
    }
}
