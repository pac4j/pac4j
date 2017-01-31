package org.pac4j.cas.client.rest;

import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.BasicAuthExtractor;
import org.pac4j.core.util.CommonHelper;

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
    protected void clientInit(final WebContext context) {
        CommonHelper.assertNotBlank("headerName", this.headerName);
        CommonHelper.assertNotNull("prefixHeader", this.prefixHeader);
        CommonHelper.assertNotNull("configuration", this.configuration);
        configuration.init(context);

        setCredentialsExtractor(new BasicAuthExtractor(this.headerName, this.prefixHeader, getName()));
        setAuthenticator(new CasRestAuthenticator(this.configuration));
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
        return CommonHelper.toString(this.getClass(), "name", getName(), "headerName", this.headerName,
                "prefixHeader", this.prefixHeader, "configuration", configuration, "extractor", getCredentialsExtractor(),
                "authenticator", getAuthenticator(), "profileCreator", getProfileCreator());
    }
}
