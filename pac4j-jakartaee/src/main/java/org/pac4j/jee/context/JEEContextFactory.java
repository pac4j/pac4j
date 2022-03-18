package org.pac4j.jee.context;

import org.pac4j.core.context.WebContextFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
