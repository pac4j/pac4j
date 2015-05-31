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

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.Gender;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;

import java.util.Locale;

/**
 * Represents basic (OAuth20Profile) profile on Ok.ru (Odnoklassniki.ru)
 *
 * @author imayka (imayka[at]ymail[dot]com)
 * @since 1.8
 */
public class OkProfile extends OAuth20Profile {

    private static final long serialVersionUID = -810631113167677397L;

    public static final String BASE_PROFILE_URL = "http://ok.ru/profile/";

    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.okDefinition;
    }

    @Override
    public String getEmail() {
        return super.getEmail();
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(OkAttributesDefinition.FIRST_NAME);
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(OkAttributesDefinition.LAST_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(OkAttributesDefinition.NAME);
    }

    @Override
    public String getUsername() {
        return (String) getAttribute(OkAttributesDefinition.UID);
    }

    @Override
    public Gender getGender() {
        return Gender.valueOf(((String) getAttribute(OkAttributesDefinition.GENDER)).toUpperCase());
    }

    @Override
    public Locale getLocale() {
        return new Locale((String) getAttribute(OkAttributesDefinition.LOCALE));
    }

    @Override
    public String getPictureUrl() {
        return (String) getAttribute(OkAttributesDefinition.PIC_1);
    }

    @Override
    public String getProfileUrl() {
        return BASE_PROFILE_URL + getId();
    }

    @Override
    public String getLocation() {
        return getAttribute(OkAttributesDefinition.LOCATION_CITY) +
                ", " +
                getAttribute(OkAttributesDefinition.LOCATION_COUNTRY);
    }
}
