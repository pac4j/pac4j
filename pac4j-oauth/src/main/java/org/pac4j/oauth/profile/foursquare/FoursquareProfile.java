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
package org.pac4j.oauth.profile.foursquare;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for Foursquare with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.FoursquareClient}.</p>
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
public class FoursquareProfile extends OAuth20Profile {
    
    private static final long serialVersionUID = 8919122885219420820L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new FoursquareAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }

    public String getBio() {
        return (String) getAttribute(FoursquareAttributesDefinition.BIO);
    }

    public FoursquareUserContact getContact() {
        return (FoursquareUserContact) getAttribute(FoursquareAttributesDefinition.CONTACT);
    }

    public FoursquareUserFriends getFriends() {
        return (FoursquareUserFriends) getAttribute(FoursquareAttributesDefinition.FIRENDS);
    }

    public FoursquareUserPhoto getPhoto() {
        return (FoursquareUserPhoto) getAttribute(FoursquareAttributesDefinition.PHOTO);
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(FoursquareAttributesDefinition.FIRST_NAME);
    }

    @Override
    public String getLocation() {
        return (String) getAttribute(FoursquareAttributesDefinition.HOME_CITY);
    }

    @Override
    public String getFamilyName() {
        return (String) getAttribute(FoursquareAttributesDefinition.LAST_NAME);
    }

    @Override
    public String getProfileUrl() {
        return "https://foursquare.com/user/" + getId();
    }

    @Override
    public String getPictureUrl() {
        return this.getPhoto().getPhotoUrl();
    }

    @Override
    public String getEmail() {
        return this.getContact().getEmail();
    }
}
