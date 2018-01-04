package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.CredentialsException;

import java.util.Optional;

/**
 * To extract header value.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class HeaderExtractor implements CredentialsExtractor<TokenCredentials> {

    private final String headerName;

    private final String prefixHeader;

    public HeaderExtractor(final String headerName, final String prefixHeader) {
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
    }

    @Override
    public Optional<TokenCredentials> extract(WebContext context) {
        return context.getRequestHeader(this.headerName).map(
            header -> {
                if (!header.startsWith(this.prefixHeader)) {
                    throw new CredentialsException("Wrong prefix for header: " + this.headerName);
                }

                final String headerWithoutPrefix = header.substring(this.prefixHeader.length());
                return new TokenCredentials(headerWithoutPrefix);
            }
        );
    }
}
