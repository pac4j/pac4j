package org.pac4j.core.profile.converter;

/**
 * This class only keeps String objects.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class StringConverter extends AbstractAttributeConverter<String> {

    public StringConverter() {
        super(String.class);
    }

    @Override
    protected String internalConvert(final Object attribute) {
        if (null != attribute) {
            return attribute.toString();
        } else {
            return null;
        }
    }
}
