package org.pac4j.jee.saml.metadata;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.jee.config.AbstractConfigFilter;
import org.pac4j.saml.client.SAML2Client;

import java.io.IOException;

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

    /** {@inheritDoc} */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);

        this.clientName = getStringParam(filterConfig, Pac4jConstants.CLIENT_NAME, this.clientName);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                  final FilterChain chain)  throws IOException, ServletException {

        CommonHelper.assertNotNull("config", getSharedConfig());
        CommonHelper.assertNotNull("clientName", clientName);

        SAML2Client client;
        val result = getSharedConfig().getClients().findClient(this.clientName);
        if (result.isPresent()) {
            client = (SAML2Client) result.get();
        } else {
            throw new TechnicalException("No SAML2 client: " + this.clientName);
        }
        client.init();
        response.getWriter().write(client.getServiceProviderMetadataResolver().getMetadata());
        response.getWriter().flush();
    }

    /** {@inheritDoc} */
    @Override
    public void destroy() {
    }

    /**
     * <p>Getter for the field <code>clientName</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * <p>Setter for the field <code>clientName</code>.</p>
     *
     * @param clientName a {@link java.lang.String} object
     */
    public void setClientName(final String clientName) {
        this.clientName = clientName;
    }
}
