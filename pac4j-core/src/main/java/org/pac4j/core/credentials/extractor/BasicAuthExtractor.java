package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.CredentialsException;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Optional;

/**
 * To extract basic auth header.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class BasicAuthExtractor implements CredentialsExtractor<UsernamePasswordCredentials> {

    private final HeaderExtractor extractor;

    public BasicAuthExtractor() {
        this(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.BASIC_HEADER_PREFIX);
    }

    public BasicAuthExtractor(final String headerName, final String prefixHeader) {
        this.extractor = new HeaderExtractor(headerName, prefixHeader);
    }

    @Override
    public Optional<UsernamePasswordCredentials> extract(WebContext context) {
        return this.extractor.extract(context).map(c -> {
            final byte[] decoded = Base64.getDecoder().decode(c.getToken());

            String token;
            try {
                token = new String(decoded, "UTF-8");
            } catch (final UnsupportedEncodingException e) {
                throw new CredentialsException("Bad format of the basic auth header");
            }

            final int delim = token.indexOf(":");
            if (delim < 0) {
                throw new CredentialsException("Bad format of the basic auth header");
            }
            return new UsernamePasswordCredentials(
                token.substring(0, delim),
                token.substring(delim + 1)
            );
        });
    }
}
