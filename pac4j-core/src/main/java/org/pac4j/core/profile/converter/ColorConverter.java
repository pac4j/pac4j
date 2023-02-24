package org.pac4j.core.profile.converter;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.profile.Color;

/**
 * This class converts a String into a Color.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
@Slf4j
public final class ColorConverter extends AbstractAttributeConverter {

    /**
     * <p>Constructor for ColorConverter.</p>
     */
    public ColorConverter() {
        super(Color.class);
    }

    /** {@inheritDoc} */
    @Override
    protected Color internalConvert(final Object attribute) {
        if (attribute instanceof String s && s.length() == 6) {
            try {
                var hex = s.substring(0, 2);
                val r = Integer.parseInt(hex, 16);
                hex = s.substring(2, 4);
                val g = Integer.parseInt(hex, 16);
                hex = s.substring(4, 6);
                val b = Integer.parseInt(hex, 16);
                return new Color(r, g, b);
            } catch (final NumberFormatException e) {
                LOGGER.error("Cannot convert " + s + " into color", e);
            }
        }
        return null;
    }
}
