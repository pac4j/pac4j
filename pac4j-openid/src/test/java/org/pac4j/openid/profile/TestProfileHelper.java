/*
  Copyright 2012 - 2014 Jerome Leleu

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
package org.pac4j.openid.profile;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.openid.profile.google.GoogleOpenIdAttributesDefinition;
import org.pac4j.openid.profile.google.GoogleOpenIdProfile;

/**
 * This class tests the {@link ProfileHelper} class for all the OpenID profiles.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class TestProfileHelper extends org.pac4j.core.profile.TestProfileHelper {
    
    public void testBuildProfileGoogleOpenIdProfile() {
        assertNotNull(ProfileHelper.buildProfile("GoogleOpenIdProfile" + "#" + STRING_ID, EMPTY_MAP));
    }
    
    @Override
    protected Class<? extends CommonProfile> getProfileClass() {
        return GoogleOpenIdProfile.class;
    }
    
    @Override
    protected String getProfileType() {
        return "GoogleOpenIdProfile";
    }
    
    @Override
    protected String getAttributeName() {
        return GoogleOpenIdAttributesDefinition.FIRSTNAME;
    }
}
