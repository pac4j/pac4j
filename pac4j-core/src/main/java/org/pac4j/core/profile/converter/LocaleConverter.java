package org.pac4j.core.profile.converter;

import java.util.Locale;

/**
 * This class converts a String to a Locale.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class LocaleConverter extends AbstractAttributeConverter {

    public LocaleConverter() {
        super(Locale.class);
    }

    @Override
    protected Locale internalConvert(final Object attribute) {
        if (attribute instanceof String) {
            final var s = ((String) attribute).replaceAll("-", "_");
            final var parts = s.split("_");
            final var length = parts.length;
            if (length == 2) {
                return new Locale(parts[0], parts[1]);
            } else if (length == 1) {
                return new Locale(parts[0]);
            }
        }
        return null;
    }
}
