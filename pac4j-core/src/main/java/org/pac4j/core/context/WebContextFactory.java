package org.pac4j.core.context;

/**
 * Build a web context from parameters.
 *
 * @author Jerome LELEU
 * @since 4.0.1
 */
@FunctionalInterface
public interface WebContextFactory {

    /**
     * <p>newContext.</p>
     *
     * @param parameters a {@link FrameworkParameters} object
     * @return a {@link WebContext} object
     */
    WebContext newContext(FrameworkParameters parameters);
}
