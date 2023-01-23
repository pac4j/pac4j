package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.CommonHelper;

import java.util.Optional;

/**
 * To extract header value.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class HeaderExtractor implements CredentialsExtractor {

    private String headerName;

    private String prefixHeader;

    private boolean trimValue;

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getPrefixHeader() {
        return prefixHeader;
    }

    public void setPrefixHeader(String prefixHeader) {
        this.prefixHeader = prefixHeader;
    }

    public boolean isTrimValue() {
        return trimValue;
    }

    public void setTrimValue(boolean trimValue) {
        this.trimValue = trimValue;
    }

    public HeaderExtractor() {
        // empty constructor as needed to be instanciated by beanutils
    }

    public HeaderExtractor(final String headerName, final String prefixHeader) {
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
    }

    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        CommonHelper.assertNotBlank("headerName", this.headerName);
        CommonHelper.assertNotNull("prefixHeader", this.prefixHeader);

        var header = ctx.webContext().getRequestHeader(this.headerName);
        if (!header.isPresent()) {
            header = ctx.webContext().getRequestHeader(this.headerName.toLowerCase());
            if (!header.isPresent()) {
                return Optional.empty();
            }
        }

        if (!header.get().startsWith(this.prefixHeader)) {
            throw new CredentialsException("Wrong prefix for header: " + this.headerName);
        }

        var headerWithoutPrefix = header.get().substring(this.prefixHeader.length());

        if (trimValue) {
            headerWithoutPrefix = headerWithoutPrefix.trim();
        }
        return Optional.of(new TokenCredentials(headerWithoutPrefix));
    }
}
