package org.pac4j.core.profile.converter;

import org.pac4j.core.profile.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class converts a String into a Color.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class ColorConverter extends AbstractAttributeConverter {

    private static final Logger logger = LoggerFactory.getLogger(ColorConverter.class);

    public ColorConverter() {
        super(Color.class);
    }

    @Override
    protected Color internalConvert(final Object attribute) {
        if (attribute instanceof String) {
            final String s = (String) attribute;
            if (s.length() == 6) {
                try {
                    String hex = s.substring(0, 2);
                    final int r = Integer.parseInt(hex, 16);
                    hex = s.substring(2, 4);
                    final int g = Integer.parseInt(hex, 16);
                    hex = s.substring(4, 6);
                    final int b = Integer.parseInt(hex, 16);
                    return new Color(r, g, b);
                } catch (final NumberFormatException e) {
                    logger.error("Cannot convert " + s + " into color", e);
                }
            }
        }
        return null;
    }
}
