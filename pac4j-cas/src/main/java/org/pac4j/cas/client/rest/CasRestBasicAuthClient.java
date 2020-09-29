package org.pac4j.cas.client.rest;

import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.extractor.BasicAuthExtractor;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * Direct client which receives credentials as a basic auth and validates them via the CAS REST API.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public class CasRestBasicAuthClient extends AbstractCasRestClient {

    private String headerName = HttpConstants.AUTHORIZATION_HEADER;

    private String prefixHeader = HttpConstants.BASIC_HEADER_PREFIX;

    public CasRestBasicAuthClient() {}

    public CasRestBasicAuthClient(final CasConfiguration configuration,
                                  final String headerName, final String prefixHeader) {
        this.configuration = configuration;
        this.headerName = headerName;
        this.prefixHeader = prefixHeader;
    }

    @Override
    protected void internalInit() {
        assertNotBlank("headerName", this.headerName);
        assertNotNull("prefixHeader", this.prefixHeader);
        assertNotNull("configuration", this.configuration);

        defaultCredentialsExtractor(new BasicAuthExtractor(this.headerName, this.prefixHeader));
        defaultAuthenticator(new CasRestAuthenticator(this.configuration));
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(final String headerName) {
        this.headerName = headerName;
    }

    public String getPrefixHeader() {
        return prefixHeader;
    }

    public void setPrefixHeader(final String prefixHeader) {
        this.prefixHeader = prefixHeader;
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "name", getName(), "credentialsExtractor", getCredentialsExtractor(),
            "authenticator", getAuthenticator(), "profileCreator", getProfileCreator(),
            "authorizationGenerators", getAuthorizationGenerators(), "configuration", this.configuration,
            "headerName", this.headerName, "prefixHeader", this.prefixHeader);
    }
}
