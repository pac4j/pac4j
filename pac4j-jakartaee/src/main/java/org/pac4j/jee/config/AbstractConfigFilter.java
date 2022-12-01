package org.pac4j.jee.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private Config config;

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

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        val req = (HttpServletRequest) request;
        val resp = (HttpServletResponse) response;

        internalFilter(req, resp, chain);
    }

    protected abstract void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                           final FilterChain chain) throws IOException, ServletException;

    public Config getSharedConfig() {
        if (this.config == null) {
            return AbstractConfigFilter.CONFIG;
        }
        return this.config;
    }

    public Config getConfig() {
        return this.config;
    }

    public void setConfig(final Config config) {
        CommonHelper.assertNotNull("config", config);
        this.config = config;
    }
}
