package org.pac4j.core.profile.converter;

import lombok.val;

import java.util.Locale;

/**
 * This class converts a String to a Locale.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class LocaleConverter extends AbstractAttributeConverter {

    /**
     * <p>Constructor for LocaleConverter.</p>
     */
    public LocaleConverter() {
        super(Locale.class);
    }

    /** {@inheritDoc} */
    @Override
    protected Locale internalConvert(final Object attribute) {
        if (attribute instanceof String str) {
            val s = str.replaceAll("-", "_");
            val parts = s.split("_");
            val length = parts.length;
            if (length == 2) {
                return new Locale(parts[0], parts[1]);
            } else if (length == 1) {
                return new Locale(parts[0]);
            }
        }
        return null;
    }
}
