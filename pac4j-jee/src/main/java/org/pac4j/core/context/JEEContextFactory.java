package org.pac4j.core.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Use the pac4j-javaee dependency instead.
 */
@Deprecated
public class JEEContextFactory implements WebContextFactory {

    public static final JEEContextFactory INSTANCE = new JEEContextFactory();

    @Override
    public JEEContext newContext(final Object... parameters) {
        return new JEEContext((HttpServletRequest) parameters[0], (HttpServletResponse) parameters[1]);
    }
}
