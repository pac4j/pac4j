package org.pac4j.jee.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.val;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * An abstract JEE filter which handles configuration.
 *
 * @author Jerome Leleu
 * @since 5.0.0
 */
public abstract class AbstractConfigFilter implements Filter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static Config CONFIG;

    @Getter
    private Config config;

    /** {@inheritDoc} */
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        val configFactoryParam = filterConfig.getInitParameter(Pac4jConstants.CONFIG_FACTORY);
        if (configFactoryParam != null) {
            val builtConfig = ConfigBuilder.build(configFactoryParam);
            if (builtConfig != null) {
                this.config = builtConfig;
                AbstractConfigFilter.CONFIG = builtConfig;
            }
        }
    }

    /**
     * <p>getStringParam.</p>
     *
     * @param filterConfig a {@link jakarta.servlet.FilterConfig} object
     * @param name a {@link java.lang.String} object
     * @param defaultValue a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    protected String getStringParam(final FilterConfig filterConfig, final String name, final String defaultValue) {
        val param = filterConfig.getInitParameter(name);
        final String value;
        if (param != null) {
            value = param;
        } else {
            value = defaultValue;
        }
        logger.debug("String param: {}: {}", name, value);
        return value;
    }

    /**
     * <p>getBooleanParam.</p>
     *
     * @param filterConfig a {@link jakarta.servlet.FilterConfig} object
     * @param name a {@link java.lang.String} object
     * @param defaultValue a {@link java.lang.Boolean} object
     * @return a {@link java.lang.Boolean} object
     */
    protected Boolean getBooleanParam(final FilterConfig filterConfig, final String name, final Boolean defaultValue) {
        val param = filterConfig.getInitParameter(name);
        final Boolean value;
        if (param != null) {
            value = Boolean.parseBoolean(param);
        } else {
            value = defaultValue;
        }
        logger.debug("Boolean param: {}: {}", name, value);
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        val req = (HttpServletRequest) request;
        val resp = (HttpServletResponse) response;

        internalFilter(req, resp, chain);
    }

    /**
     * <p>internalFilter.</p>
     *
     * @param request a {@link jakarta.servlet.http.HttpServletRequest} object
     * @param response a {@link jakarta.servlet.http.HttpServletResponse} object
     * @param chain a {@link jakarta.servlet.FilterChain} object
     * @throws java.io.IOException if any.
     * @throws jakarta.servlet.ServletException if any.
     */
    protected abstract void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                           final FilterChain chain) throws IOException, ServletException;

    /**
     * <p>getSharedConfig.</p>
     *
     * @return a {@link org.pac4j.core.config.Config} object
     */
    public Config getSharedConfig() {
        if (this.config == null) {
            return AbstractConfigFilter.CONFIG;
        }
        return this.config;
    }

    /**
     * <p>Setter for the field <code>config</code>.</p>
     *
     * @param config a {@link org.pac4j.core.config.Config} object
     */
    public void setConfig(final Config config) {
        CommonHelper.assertNotNull("config", config);
        this.config = config;
    }
}
