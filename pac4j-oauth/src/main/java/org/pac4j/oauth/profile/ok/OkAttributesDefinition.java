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
 * This class defines the attributes of the Vk profile.
 *
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 */
public class OkAttributesDefinition extends OAuthAttributesDefinition {

    public static final String UID = "uid";
    public static final String BIRTHDAY = "birthday";
    public static final String AGE = "age";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String NAME = "name";
    public static final String LOCALE = "locale";
    public static final String GENDER = "gender";
    public static final String HAS_EMAIL = "has_email";
    public static final String LOCATION = "location";
    public static final String CITY = "city";
    public static final String CURRENT_STATUS = "current_status";
    public static final String CURRENT_STATUS_ID = "current_status_id";
    public static final String CURRENT_STATUS_DATE = "current_status_date";
    public static final String ONLINE = "online";
    public static final String PIC_1 = "pic_1";
    public static final String PIC_2 = "pic_2";


    public OkAttributesDefinition() {
        addAttribute(UID, Converters.stringConverter);
        addAttribute(BIRTHDAY, Converters.stringConverter);
        addAttribute(AGE, Converters.stringConverter);
        addAttribute(FIRST_NAME, Converters.stringConverter);
        addAttribute(LAST_NAME, Converters.stringConverter);
        addAttribute(NAME, Converters.stringConverter);
        addAttribute(LOCALE, Converters.stringConverter);
        addAttribute(GENDER, Converters.stringConverter);
        addAttribute(HAS_EMAIL, Converters.stringConverter);
        addAttribute(LOCATION, Converters.stringConverter);
        addAttribute(CITY, Converters.stringConverter);
        addAttribute(CURRENT_STATUS, Converters.stringConverter);
        addAttribute(CURRENT_STATUS_ID, Converters.stringConverter);
        addAttribute(CURRENT_STATUS_DATE, Converters.stringConverter);
        addAttribute(ONLINE, Converters.stringConverter);
        addAttribute(PIC_1, Converters.stringConverter);
        addAttribute(PIC_2, Converters.stringConverter);

    }
}
