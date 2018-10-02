package org.pac4j.core.state;

import org.pac4j.core.context.WebContext;

/**
 * State generator.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
public interface StateGenerator {

    String generateState(WebContext webContext);
}
