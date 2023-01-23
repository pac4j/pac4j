package org.pac4j.core.util.generator;

import org.pac4j.core.context.CallContext;

/**
 * Value generator.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@FunctionalInterface
public interface ValueGenerator {

    String generateValue(CallContext ctx);
}
