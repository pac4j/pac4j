package org.pac4j.core.profile.converter;

/**
 * This class converts a String into an Integer or returns the Integer in input.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class IntegerConverter extends AbstractAttributeConverter {

    /**
     * <p>Constructor for IntegerConverter.</p>
     */
    public IntegerConverter() {
        super(Integer.class);
    }

    /** {@inheritDoc} */
    @Override
    protected Integer internalConvert(final Object attribute) {
        if (attribute instanceof String) {
            return Integer.parseInt((String) attribute);
        }
        return null;
    }
}
