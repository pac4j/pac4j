package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.credentials.TokenCredentials;

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
    public TokenCredentials extract(WebContext context) {
        final String header = context.getRequestHeader(this.headerName);
        if (header == null) {
            return null;
        }

        if  (!header.startsWith(this.prefixHeader)) {
            throw new CredentialsException("Wrong prefix for header: " + this.headerName);
        }

        final String headerWithoutPrefix = header.substring(this.prefixHeader.length());
        return new TokenCredentials(headerWithoutPrefix);
    }
}
