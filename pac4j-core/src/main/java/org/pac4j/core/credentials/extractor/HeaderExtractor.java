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

    /**
     * <p>Getter for the field <code>headerName</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getHeaderName() {
        return headerName;
    }

    /**
     * <p>Setter for the field <code>headerName</code>.</p>
     *
     * @param headerName a {@link String} object
     */
    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    /**
     * <p>Getter for the field <code>prefixHeader</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getPrefixHeader() {
        return prefixHeader;
    }

    /**
     * <p>Setter for the field <code>prefixHeader</code>.</p>
     *
     * @param prefixHeader a {@link String} object
     */
    public void setPrefixHeader(String prefixHeader) {
        this.prefixHeader = prefixHeader;
    }

    /**
     * <p>isTrimValue.</p>
     *
     * @return a boolean
     */
    public boolean isTrimValue() {
        return trimValue;
    }

    /**
     * <p>Setter for the field <code>trimValue</code>.</p>
     *
     * @param trimValue a boolean
     */
    public void setTrimValue(boolean trimValue) {
        this.trimValue = trimValue;
    }

    /**
     * <p>Constructor for HeaderExtractor.</p>
     */
    public HeaderExtractor() {
        // empty constructor as needed to be instanciated by beanutils
    }

    /**
     * <p>Constructor for HeaderExtractor.</p>
     *
     * @param headerName a {@link String} object
     * @param prefixHeader a {@link String} object
     */
    public HeaderExtractor(final String headerName, final String prefixHeader) {
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> extract(final CallContext ctx) {
        CommonHelper.assertNotBlank("headerName", this.headerName);
        CommonHelper.assertNotNull("prefixHeader", this.prefixHeader);

        var header = ctx.webContext().getRequestHeader(this.headerName);
        if (header.isEmpty()) {
            header = ctx.webContext().getRequestHeader(this.headerName.toLowerCase());
            if (header.isEmpty()) {
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
