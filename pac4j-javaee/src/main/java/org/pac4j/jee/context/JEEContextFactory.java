package org.pac4j.jee.context;

import org.pac4j.core.context.FrameworkParameters;
import org.pac4j.core.context.WebContextFactory;
import org.pac4j.core.exception.TechnicalException;

/**
 * Build a JEE context from parameters.
 *
 * @author Jerome LELEU
 * @since 4.0.1
 */
public class JEEContextFactory implements WebContextFactory {

    public static final JEEContextFactory INSTANCE = new JEEContextFactory();

    @Override
    public JEEContext newContext(final FrameworkParameters parameters) {
        if (parameters instanceof JEEFrameworkParameters jeeFrameworkParameters) {
            return new JEEContext(jeeFrameworkParameters.getRequest(), jeeFrameworkParameters.getResponse());
        }
        throw new TechnicalException("Bad parameters type");
    }
}
