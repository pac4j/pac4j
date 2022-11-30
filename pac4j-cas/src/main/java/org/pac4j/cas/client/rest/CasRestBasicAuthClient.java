package org.pac4j.cas.client.rest;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.extractor.BasicAuthExtractor;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * Direct client which receives credentials as a basic auth and validates them via the CAS REST API.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
@Getter
@Setter
@ToString(callSuper = true)
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
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("headerName", this.headerName);
        assertNotNull("prefixHeader", this.prefixHeader);
        assertNotNull("configuration", this.configuration);

        defaultCredentialsExtractor(new BasicAuthExtractor(this.headerName, this.prefixHeader));
        defaultAuthenticator(new CasRestAuthenticator(this.configuration));
    }
}
