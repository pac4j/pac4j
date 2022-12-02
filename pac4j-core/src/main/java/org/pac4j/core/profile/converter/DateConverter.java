package org.pac4j.core.profile.converter;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * This class converts a String (depending on a specified format) into a Date.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
@Slf4j
public class DateConverter extends AbstractAttributeConverter {

    protected String format;

    protected Locale locale;

    public DateConverter() {
        this(DateTimeFormatter.ISO_LOCAL_DATE_TIME.toString());
    }

    public DateConverter(final String format) {
        super(Date.class);
        this.format = format;
    }

    public DateConverter(final String format, final Locale locale) {
        this(format);
        this.locale = locale;
    }

    @Override
    protected Date internalConvert(final Object attribute) {
        if (attribute instanceof String s) {
            SimpleDateFormat simpleDateFormat;
            if (this.locale == null) {
                simpleDateFormat = new SimpleDateFormat(this.format);
            } else {
                simpleDateFormat = new SimpleDateFormat(this.format, this.locale);
            }
            try {
                return simpleDateFormat.parse(s);
            } catch (final ParseException e) {
                LOGGER.error("parse exception on {} with format: {} and locale: {}", s, this.format, this.locale, e);
            }
        }
        return null;
    }
}
