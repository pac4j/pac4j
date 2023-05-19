package org.pac4j.core.profile.converter;

/**
 * This class defines the default converters.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class Converters {

    /** Constant <code>LOCALE</code> */
    public final static AttributeConverter LOCALE = new LocaleConverter();

    /** Constant <code>STRING</code> */
    public final static AttributeConverter STRING = new StringConverter();

    /** Constant <code>BOOLEAN</code> */
    public final static AttributeConverter BOOLEAN = new BooleanConverter();

    /** Constant <code>INTEGER</code> */
    public final static AttributeConverter INTEGER = new IntegerConverter();

    /** Constant <code>LONG</code> */
    public final static AttributeConverter LONG = new LongConverter();

    /** Constant <code>COLOR</code> */
    public final static AttributeConverter COLOR = new ColorConverter();

    /** Constant <code>GENDER</code> */
    public final static AttributeConverter GENDER = new GenderConverter();

    /** Constant <code>DATE_TZ_GENERAL_FORMAT="yyyy-MM-dd'T'HH:mm:ssz"</code> */
    public final static String DATE_TZ_GENERAL_FORMAT = "yyyy-MM-dd'T'HH:mm:ssz";
    /** Constant <code>DATE_TZ_GENERAL</code> */
    public final static AttributeConverter DATE_TZ_GENERAL = new DateConverter(DATE_TZ_GENERAL_FORMAT);

    /** Constant <code>DATE_TZ_RFC822_FORMAT="yyyy-MM-dd'T'HH:mm:ss'Z'"</code> */
    public final static String DATE_TZ_RFC822_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    /** Constant <code>DATE_TZ_RFC822</code> */
    public final static AttributeConverter DATE_TZ_RFC822 = new DateConverter(DATE_TZ_RFC822_FORMAT);

    /** Constant <code>URL</code> */
    public final static AttributeConverter URL = new UrlConverter();
}
