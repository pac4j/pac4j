package org.pac4j.javaee.saml.metadata;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.javaee.config.AbstractConfigFilter;
import org.pac4j.saml.client.SAML2Client;

/**
 * This filter prints the SP metadata for SAML.
 *
 * Example shiro.ini configuration:
 *
 * saml2MetadataFilter = org.pac4j.saml.metadata.Saml2MetadataFilter
 * saml2MetadataFilter.config = $config
 * saml2MetadataFilter.clientName = SAML2Client
 *
 * [urls]
 * /API/SAML2/metadata = saml2MetadataFilter
 *
 * @author Graham Leggett
 * @since 3.8.0
 */
public class Saml2MetadataFilter extends AbstractConfigFilter {

    private String clientName;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);

        this.clientName = getStringParam(filterConfig, Pac4jConstants.CLIENT_NAME, this.clientName);
    }

    @Override
    protected void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                  final FilterChain chain)  throws IOException, ServletException {

        CommonHelper.assertNotNull("config", getSharedConfig());
        CommonHelper.assertNotNull("clientName", clientName);

        SAML2Client client;
        final var result = getSharedConfig().getClients().findClient(this.clientName);
        if (result.isPresent()) {
            client = (SAML2Client) result.get();
        } else {
            throw new TechnicalException("No SAML2 client: " + this.clientName);
        }
        client.init();
        response.getWriter().write(client.getServiceProviderMetadataResolver().getMetadata());
        response.getWriter().flush();
    }

    @Override
    public void destroy() {
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(final String clientName) {
        this.clientName = clientName;
    }
}
