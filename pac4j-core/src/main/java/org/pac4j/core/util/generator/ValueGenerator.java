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

    /**
     * <p>generateValue.</p>
     *
     * @param ctx a {@link CallContext} object
     * @return a {@link String} object
     */
    String generateValue(CallContext ctx);
}
