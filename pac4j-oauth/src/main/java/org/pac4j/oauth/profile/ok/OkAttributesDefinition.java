/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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
        primary(UID, Converters.stringConverter);
        primary(BIRTHDAY, Converters.stringConverter);
        primary(AGE, Converters.stringConverter);
        primary(NAME, Converters.stringConverter);
        primary(LOCALE, Converters.stringConverter);
        primary(GENDER, Converters.stringConverter);
        primary(LOCATION_CITY, Converters.stringConverter);
        primary(LOCATION_COUNTRY, Converters.stringConverter);
        primary(LOCATION_COUNTRY_CODE, Converters.stringConverter);
        primary(LOCATION_COUNTRY_NAME, Converters.stringConverter);
        primary(ONLINE, Converters.stringConverter);
        primary(FIRST_NAME, Converters.stringConverter);
        primary(LAST_NAME, Converters.stringConverter);
        primary(HAS_EMAIL, Converters.stringConverter);
        primary(CURRENT_STATUS, Converters.stringConverter);
        primary(CURRENT_STATUS_ID, Converters.stringConverter);
        primary(CURRENT_STATUS_DATE, Converters.stringConverter);
        primary(PIC_1, Converters.stringConverter);
        primary(PIC_2, Converters.stringConverter);

    }
}
