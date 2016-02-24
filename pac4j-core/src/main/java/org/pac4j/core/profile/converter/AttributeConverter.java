package org.pac4j.core.profile.converter;

/**
 * This interface is the contract for an attribute converter.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public interface AttributeConverter<T extends Object> {
    
    /**
     * Convert an attribute to a specific type T.
     * 
     * @param attribute attribute
     * @return the converted attribute
     */
    T convert(Object attribute);
}
