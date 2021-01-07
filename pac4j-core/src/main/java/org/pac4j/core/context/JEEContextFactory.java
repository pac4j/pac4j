package org.pac4j.core.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Build a JEE context from parameters.
 *
 * @author Jerome LELEU
 * @since 4.0.1
 */
public class JEEContextFactory implements WebContextFactory {

    public static final JEEContextFactory INSTANCE = new JEEContextFactory();

    @Override
    public JEEContext newContext(final Object... parameters) {
        return new JEEContext((HttpServletRequest) parameters[0], (HttpServletResponse) parameters[1]);
    }
}
