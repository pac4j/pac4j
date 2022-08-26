package org.pac4j.jee.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pac4j.core.context.WebContextFactory;

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
        var request = (HttpServletRequest) parameters[0];
        var response = (HttpServletResponse) parameters[1];
        return new JEEContext(request, response);
    }
}
