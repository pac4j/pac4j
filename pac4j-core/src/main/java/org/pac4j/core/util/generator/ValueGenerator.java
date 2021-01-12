package org.pac4j.core.util.generator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;

/**
 * Value generator.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public interface ValueGenerator {

    String generateValue(WebContext webContext, SessionStore sessionStore);
}
