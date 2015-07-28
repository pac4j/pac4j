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
package org.pac4j.cas.profile;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;

/**
 * This class tests the {@link ProfileHelper} class for the {@link CasProfile}.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class TestCasProfileHelper extends org.pac4j.core.profile.TestProfileHelper {
    
    @Override
    protected Class<? extends CommonProfile> getProfileClass() {
        return CasProfile.class;
    }
    
    public void testBuildProfileCasProxyProfile() {
        assertNotNull(ProfileHelper.buildProfile("CasProxyProfile" + "#" + STRING_ID, EMPTY_MAP));
    }
    
    @Override
    protected String getProfileType() {
        return "CasProfile";
    }
    
    @Override
    protected String getAttributeName() {
        return "whatever";
    }
}
