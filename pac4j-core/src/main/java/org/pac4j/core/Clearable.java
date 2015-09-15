package org.pac4j.core;

/**
 * Implementing classes have the ability to clear out sensitive
 * or unnecessary information.
 *
 * @author  Jacob Severson
 * @since   1.8.0
 */
public interface Clearable {

    /**
     * Removes any sensitive or unnecessary information held in the object.
     */
    void clear();
}
