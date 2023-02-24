package org.pac4j.oidc.profile.converter;

import org.pac4j.core.profile.converter.AttributeConverter;

import java.util.Date;

/**
 * Convert a number of seconds into a Date.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcLongTimeConverter implements AttributeConverter {

    /** {@inheritDoc} */
    @Override
    public Date convert(final Object attribute) {
        if (attribute instanceof Long) {
            final long seconds = (Long) attribute;
            return new Date(seconds * 1000);
        }
        return null;
    }
}
