package org.pac4j.core.profile.converter;

import java.util.Date;
import java.util.Locale;

import org.pac4j.core.profile.FormattedDate;

/**
 * This class converts a String (depending on a specified format) into a FormattedDate.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class FormattedDateConverter extends DateConverter {
    
    public FormattedDateConverter(final String format) {
        super(format);
    }
    
    public FormattedDateConverter(final String format, final Locale locale) {
        super(format, locale);
    }
    
    @Override
    public FormattedDate convert(final Object attribute) {
        final Date result = super.convert(attribute);
        if (result != null) {
            return new FormattedDate(result, this.format, this.locale);
        }
        return null;
    }
}
