package org.pac4j.core.util;

/**
 * A procedure (no input, no result).
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
@FunctionalInterface
public interface Procedure {

    void execute() throws Exception;
}
