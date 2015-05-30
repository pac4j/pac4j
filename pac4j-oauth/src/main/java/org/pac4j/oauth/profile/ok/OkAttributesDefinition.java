package org.pac4j.oauth.profile.ok;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * Represents attributes definitions of user profile on Ok.ru (Odnoklassniki.ru)
 *
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public class OkAttributesDefinition extends OAuthAttributesDefinition {

    public static final String UID = "uid";
    public static final String BIRTHDAY = "birthday";
    public static final String AGE = "age";
    public static final String NAME = "name";
    public static final String LOCALE = "locale";
    public static final String GENDER = "gender";
    public static final String LOCATION_CITY = "location.city";
    public static final String LOCATION_COUNTRY = "location.country";
    public static final String LOCATION_COUNTRY_CODE = "location.countryCode";
    public static final String LOCATION_COUNTRY_NAME = "location.countryName";
    public static final String ONLINE = "online";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String HAS_EMAIL = "has_email";
    public static final String CURRENT_STATUS = "current_status";
    public static final String CURRENT_STATUS_ID = "current_status_id";
    public static final String CURRENT_STATUS_DATE = "current_status_date";
    public static final String PIC_1 = "pic_1";
    public static final String PIC_2 = "pic_2";


    public OkAttributesDefinition() {
        addAttribute(UID, Converters.stringConverter);
        addAttribute(BIRTHDAY, Converters.stringConverter);
        addAttribute(AGE, Converters.stringConverter);
        addAttribute(NAME, Converters.stringConverter);
        addAttribute(LOCALE, Converters.stringConverter);
        addAttribute(GENDER, Converters.stringConverter);
        addAttribute(LOCATION_CITY, Converters.stringConverter);
        addAttribute(LOCATION_COUNTRY, Converters.stringConverter);
        addAttribute(LOCATION_COUNTRY_CODE, Converters.stringConverter);
        addAttribute(LOCATION_COUNTRY_NAME, Converters.stringConverter);
        addAttribute(ONLINE, Converters.stringConverter);
        addAttribute(FIRST_NAME, Converters.stringConverter);
        addAttribute(LAST_NAME, Converters.stringConverter);
        addAttribute(HAS_EMAIL, Converters.stringConverter);
        addAttribute(CURRENT_STATUS, Converters.stringConverter);
        addAttribute(CURRENT_STATUS_ID, Converters.stringConverter);
        addAttribute(CURRENT_STATUS_DATE, Converters.stringConverter);
        addAttribute(PIC_1, Converters.stringConverter);
        addAttribute(PIC_2, Converters.stringConverter);

    }
}
