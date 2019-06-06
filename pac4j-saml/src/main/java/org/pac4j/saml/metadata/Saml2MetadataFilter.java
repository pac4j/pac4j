package org.pac4j.saml.metadata;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.pac4j.core.config.Config;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.client.SAML2Client;

/**
 * This filter prints the SP metadata for SAML.
 * 
 * Example shiro.ini configuration:
 * 
 * saml2MetadataFilter = org.pac4j.saml.metadata.Saml2MetadataFilter
 * saml2MetadataFilter.config = $config
 * saml2MetadataFilter.client = SAML2Client
 * 
 * [urls]
 * /API/SAML2/metadata = saml2MetadataFilter
 * 
 * @author Graham Leggett
 */
public class Saml2MetadataFilter implements Filter {

    private Config config;

    private String client;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {

        CommonHelper.assertNotNull("config", config);
        CommonHelper.assertNotNull("client", client);

        SAML2Client client = (SAML2Client) config.getClients().findClient(this.client).get();
        if (client != null) {
            client.init();
            servletResponse.getWriter().write(client.getServiceProviderMetadataResolver().getMetadata());
            servletResponse.getWriter().flush();
        } else {
            throw new TechnicalException("No SAML2Client: " + this.client);
        }
    }

    @Override
    public void destroy() {
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(final Config config) {
        this.config = config;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

}
