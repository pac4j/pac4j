package org.pac4j.oauth.profile.ok;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

import java.util.Arrays;

/**
 * Represents the profile definitions on Ok.ru (Odnoklassniki.ru)
 *
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public class OkProfileDefinition extends CommonProfileDefinition<OkProfile> {

    public static final String UID = "uid";
    public static final String BIRTHDAY = "birthday";
    public static final String AGE = "age";
    public static final String NAME = "name";
    public static final String LOCATION_CITY = "location.city";
    public static final String LOCATION_COUNTRY = "location.country";
    public static final String LOCATION_COUNTRY_CODE = "location.countryCode";
    public static final String LOCATION_COUNTRY_NAME = "location.countryName";
    public static final String ONLINE = "online";
    public static final String LAST_NAME = "last_name";
    public static final String HAS_EMAIL = "has_email";
    public static final String CURRENT_STATUS = "current_status";
    public static final String CURRENT_STATUS_ID = "current_status_id";
    public static final String CURRENT_STATUS_DATE = "current_status_date";
    public static final String PIC_1 = "pic_1";
    public static final String PIC_2 = "pic_2";


    public OkProfileDefinition() {
        super(x -> new OkProfile());
        Arrays.stream(new String[] {UID, BIRTHDAY, AGE, NAME, LOCATION_CITY, LOCATION_COUNTRY, LOCATION_COUNTRY_CODE,
                LOCATION_COUNTRY_NAME, ONLINE, LAST_NAME, HAS_EMAIL, CURRENT_STATUS, CURRENT_STATUS_ID, CURRENT_STATUS_DATE})
                .forEach(a -> primary(a, Converters.STRING));
        primary(PIC_1, Converters.URL);
        primary(PIC_2, Converters.URL);
    }
}
