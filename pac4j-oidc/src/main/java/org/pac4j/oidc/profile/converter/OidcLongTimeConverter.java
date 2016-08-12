package org.pac4j.oidc.profile.converter;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.FormattedDate;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.profile.converter.Converters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Convert a number of seconds into a Date.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcLongTimeConverter implements AttributeConverter<Date> {

    @Override
    public Date convert(final Object attribute) {
        if (attribute instanceof Long) {
            final long seconds = (Long) attribute;
            return new FormattedDate(new Date(seconds * 1000), Converters.DATE_TZ_GENERAL_FORMAT, Locale.getDefault());
        } else if (attribute instanceof String) {
            final SimpleDateFormat sdf = new SimpleDateFormat(Converters.DATE_TZ_GENERAL_FORMAT);
            try {
                return new FormattedDate(sdf.parse((String) attribute), Converters.DATE_TZ_GENERAL_FORMAT, Locale.getDefault());
            } catch (final ParseException e) {
                throw new TechnicalException(e);
            }
        } else if (attribute instanceof FormattedDate) {
            return (Date) attribute;
        }
        return null;
    }
}
