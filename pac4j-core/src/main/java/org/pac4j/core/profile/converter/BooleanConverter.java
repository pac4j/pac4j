package org.pac4j.core.profile.converter;

/**
 * This class converts a String into a Boolean or returns the Boolean in input.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class BooleanConverter extends AbstractAttributeConverter {

    /**
     * <p>Constructor for BooleanConverter.</p>
     */
    public BooleanConverter() {
        super(Boolean.class);
    }

    /** {@inheritDoc} */
    @Override
    protected Boolean internalConvert(final Object attribute) {
        if (attribute instanceof String) {
            return "1".equals(attribute) || "true".equals(attribute);
        } else if (attribute instanceof Number) {
            return Integer.valueOf(1).equals(attribute);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    protected Boolean defaultValue() {
        return Boolean.FALSE;
    }
}
