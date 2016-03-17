package org.pac4j.core.profile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class represents a formatted date.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class FormattedDate extends Date {
    
    private static final long serialVersionUID = 7721389956184262608L;
    
    private String format;
    
    private Locale locale;
    
    public FormattedDate() {
    }
    
    public FormattedDate(final Date date, final String format, final Locale locale) {
        super(date.getTime());
        this.format = format;
        this.locale = locale;
    }
    
    public String getFormat() {
        return this.format;
    }
    
    public void setFormat(final String format) {
        this.format = format;
    }
    
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }
    
    public Locale getLocale() {
        return this.locale;
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat;
        if (this.locale == null) {
            simpleDateFormat = new SimpleDateFormat(this.format);
        } else {
            simpleDateFormat = new SimpleDateFormat(this.format, this.locale);
        }
        return simpleDateFormat.format(this);
    }
}
