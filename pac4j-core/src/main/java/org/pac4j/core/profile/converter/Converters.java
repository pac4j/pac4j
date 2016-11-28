package org.pac4j.core.profile.converter;

/**
 * This class defines the default converters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class Converters {
    
    public final static LocaleConverter LOCALE = new LocaleConverter();
    
    public final static StringConverter STRING = new StringConverter();
    
    public final static BooleanConverter BOOLEAN = new BooleanConverter();
    
    public final static IntegerConverter INTEGER = new IntegerConverter();
    
    public final static LongConverter LONG = new LongConverter();
    
    public final static ColorConverter COLOR = new ColorConverter();
    
    public final static GenderConverter GENDER = new GenderConverter();

    public final static String DATE_TZ_GENERAL_FORMAT = "yyyy-MM-dd'T'HH:mm:ssz";
    public final static DateConverter DATE_TZ_GENERAL = new DateConverter(DATE_TZ_GENERAL_FORMAT);

    public final static String DATE_TZ_RFC822_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public final static DateConverter DATE_TZ_RFC822 = new DateConverter(DATE_TZ_RFC822_FORMAT);

    public final static UrlConverter URL = new UrlConverter();
}
