package org.pac4j.core.profile.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class converts a String (depending on a specified format) into a Date.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class DateConverter extends AbstractAttributeConverter<Date> {
    
    protected static final Logger logger = LoggerFactory.getLogger(DateConverter.class);
    
    protected String format;
    
    protected Locale locale;
    
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
        if (attribute instanceof String) {
            final SimpleDateFormat simpleDateFormat;
            if (this.locale == null) {
                simpleDateFormat = new SimpleDateFormat(this.format);
            } else {
                simpleDateFormat = new SimpleDateFormat(this.format, this.locale);
            }
            final String s = (String) attribute;
            try {
                return simpleDateFormat.parse(s);
            } catch (final ParseException e) {
                logger.error("parse exception on {} with format: {} and locale: {}", s, this.format, this.locale, e);
            }
        }
        return null;
    }
}
