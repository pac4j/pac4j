package org.pac4j.core.credentials.extractor;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;

/**
 * To extract header value.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class HeaderExtractor implements CredentialsExtractor<TokenCredentials> {

    private String headerName;

    private String prefixHeader;

    private String clientName;

    private boolean trimValue = false;

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

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Boolean getTrimValue() {
        return trimValue;
    }

    public void setTrimValue(Boolean trimValue) {
        this.trimValue = trimValue;
    }

    public HeaderExtractor() {
        // empty constructor as needed to be instanciated by beanutils
    }

    public HeaderExtractor(final String headerName, final String prefixHeader, final String clientName) {
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
        this.clientName = clientName;
    }

    @Override
    public TokenCredentials extract(WebContext context) throws HttpAction, CredentialsException {
        CommonHelper.assertNotBlank("headerName", this.headerName);
        CommonHelper.assertNotNull("prefixHeader", this.prefixHeader);

        String header = context.getRequestHeader(this.headerName);
        if (header == null) {
            header = context.getRequestHeader(this.headerName.toLowerCase());
            if (header == null) {
                return null;
            }
        }

        if (!header.startsWith(this.prefixHeader)) {
            throw new CredentialsException("Wrong prefix for header: " + this.headerName);
        }

        String headerWithoutPrefix = header.substring(this.prefixHeader.length());

        if (trimValue) {
            headerWithoutPrefix = headerWithoutPrefix.trim();
        }
        return new TokenCredentials(headerWithoutPrefix, clientName);
    }
}
