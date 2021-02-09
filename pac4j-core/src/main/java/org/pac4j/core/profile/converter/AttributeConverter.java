package org.pac4j.core.profile.converter;

/**
 * This interface is the contract for an attribute converter.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
@FunctionalInterface
public interface AttributeConverter {

    /**
     * Convert an attribute to a specific type.
     *
     * @param attribute attribute
     * @return the converted attribute
     */
    Object convert(Object attribute);
}
