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
package org.pac4j.gae.profile;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;

/**
 * This class tests the {@link ProfileHelper} class for the appengine client.
 * 
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public final class TestProfileHelper extends org.pac4j.core.profile.TestProfileHelper {

    
    @Override
    protected Class<? extends CommonProfile> getProfileClass() {
        return GaeUserServiceProfile.class;
    }
    
    @Override
    protected String getProfileType() {
        return "GaeUserServiceProfile";
    }
    
    @Override
    protected String getAttributeName() {
        return GaeUserServiceAttributesDefinition.DISPLAYNAME;
    }
}
