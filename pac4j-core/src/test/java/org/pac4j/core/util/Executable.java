package org.pac4j.core.util;

/**
 * Something executable (no input, no result, maybe an exception).
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
@FunctionalInterface
public interface Executable {

    void execute() throws Exception;
}
