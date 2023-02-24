package org.pac4j.core.profile.converter;

/**
 * This class only keeps String objects.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class StringConverter extends AbstractAttributeConverter {

    /**
     * <p>Constructor for StringConverter.</p>
     */
    public StringConverter() {
        super(String.class);
    }
}
