package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.HttpAction;

/**
 * To extract header value.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class HeaderExtractor implements CredentialsExtractor<TokenCredentials> {

    private final String headerName;

    private final String prefixHeader;

    private final String clientName;

    public HeaderExtractor(final String headerName, final String prefixHeader, final String clientName) {
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
        this.clientName = clientName;
    }

    @Override
    public TokenCredentials extract(WebContext context) throws HttpAction {
        final String header = context.getRequestHeader(this.headerName);
        if (header == null) {
            return null;
        }

        if (!header.startsWith(this.prefixHeader)) {
            throw new CredentialsException("Wrong prefix for header: \"" + this.headerName + ": " + header
                    + "\",Expected matching RegExp : \"" + this.headerName + ": " + this.prefixHeader + ".*\"");
        }

        final String headerWithoutPrefix = header.substring(this.prefixHeader.length());
        return new TokenCredentials(headerWithoutPrefix, clientName);
    }
}
