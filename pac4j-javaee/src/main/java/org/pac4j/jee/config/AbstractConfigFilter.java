package org.pac4j.jee.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract JEE filter which handles configuration.
 *
 * @author Jerome Leleu
 * @since 5.0.0
 */
public abstract class AbstractConfigFilter implements Filter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Config config;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        final var configFactoryParam = filterConfig.getInitParameter(Pac4jConstants.CONFIG_FACTORY);
        if (configFactoryParam != null) {
            final var config = ConfigBuilder.build(configFactoryParam);
            setSharedConfig(config);
        }
    }

    protected String getStringParam(final FilterConfig filterConfig, final String name, final String defaultValue) {
        final var param = filterConfig.getInitParameter(name);
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
        final var param = filterConfig.getInitParameter(name);
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
        final var req = (HttpServletRequest) request;
        final var resp = (HttpServletResponse) response;

        internalFilter(req, resp, chain);
    }

    protected abstract void internalFilter(final HttpServletRequest request, final HttpServletResponse response,
                                           final FilterChain chain) throws IOException, ServletException;

    @Override
    public void destroy() {}

    public Config getSharedConfig() {
        if (this.config == null) {
            return Config.INSTANCE;
        }
        return this.config;
    }

    public void setSharedConfig(final Config config) {
        CommonHelper.assertNotNull("config", config);
        this.config = config;
        Config.setConfig(config);
    }

    public Config getConfig() {
        return this.config;
    }

    public void setConfig(final Config config) {
        CommonHelper.assertNotNull("config", config);
        this.config = config;
    }
}
