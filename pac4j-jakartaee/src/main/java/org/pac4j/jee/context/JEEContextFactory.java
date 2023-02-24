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

    /** Constant <code>INSTANCE</code> */
    public static final JEEContextFactory INSTANCE = new JEEContextFactory();

    /** {@inheritDoc} */
    @Override
    public JEEContext newContext(final FrameworkParameters parameters) {
        if (parameters instanceof JEEFrameworkParameters jeeFrameworkParameters) {
            return new JEEContext(jeeFrameworkParameters.getRequest(), jeeFrameworkParameters.getResponse());
        }
        throw new TechnicalException("Bad parameters type");
    }
}
