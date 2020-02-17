package org.pac4j.core.util.generator;

import org.pac4j.core.context.WebContext;

/**
 * Value generator.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
public interface ValueGenerator {

    String generateValue(WebContext webContext);
}
