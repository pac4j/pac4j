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
package org.pac4j.oauth.profile.google2;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for Google (using OAuth protocol version 2) with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.Google2Client}.</p>
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2Profile extends OAuth20Profile {

    private static final long serialVersionUID = -7486869356444327783L;

    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.google2Definition;
    }

    @Override
    public String getEmail() {
        final List<Google2Email> list = getEmails();
        if (list != null && !list.isEmpty()) {
            return list.get(0).getEmail();
        } else {
            return null;
        }
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(Google2AttributesDefinition.GIVEN_NAME);
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(Google2AttributesDefinition.FAMILY_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(Google2AttributesDefinition.DISPLAY_NAME);
    }

    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(Google2AttributesDefinition.LANGUAGE);
    }

    @Override
    public String getPictureUrl() {
        return (String) getAttribute(Google2AttributesDefinition.PICTURE);
    }

    @Override
    public String getProfileUrl() {
        return (String) getAttribute(Google2AttributesDefinition.URL);
    }

    public Date getBirthday() {
        return (Date) getAttribute(Google2AttributesDefinition.BIRTHDAY);
    }

    @SuppressWarnings("unchecked")
    public List<Google2Email> getEmails() {
        return (List<Google2Email>) getAttribute(Google2AttributesDefinition.EMAILS);
    }
}
