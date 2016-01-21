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
package org.pac4j.oidc.profile;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oidc.profile.google.GoogleOidcAttributesDefinition;

/**
 * <p>This class is the user profile for Google (using OpenID Connect protocol) with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oidc.client.GoogleOidcClient}.</p>
 *
 * @author Jerome Leleu
 * @version 1.9.0
 */
public class GoogleOidcProfile extends OidcProfile {

    private static final long serialVersionUID = -6076954328349948251L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new GoogleOidcAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(GoogleOidcAttributesDefinition.GIVEN_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(GoogleOidcAttributesDefinition.NAME);
    }

    @Override
    public String getPictureUrl() {
        return (String) getAttribute(GoogleOidcAttributesDefinition.PICTURE);
    }

    @Override
    public String getProfileUrl() {
        return (String) getAttribute(GoogleOidcAttributesDefinition.PROFILE);
    }

    public Boolean getEmailVerified() {
        return (Boolean) getAttribute(GoogleOidcAttributesDefinition.EMAIL_VERIFIED);
    }
}
