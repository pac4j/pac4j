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
package org.pac4j.http.profile;

import java.util.HashMap;
import java.util.Map;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.TestCaseProfileHelper;
import org.pac4j.core.profile.UserProfile;

/**
 * This class tests the {@link ProfileHelper} class for a custom {@link CustomProfileClass}.
 * 
 * @author Jerome Leleu
 * @since 1.8.9
 */
public final class TestCustomProfileHelper extends TestCaseProfileHelper {
    
    @Override
    protected Class<? extends CommonProfile> getProfileClass() {
        return CustomProfileClass.class;
    }
    
    @Override
    protected String getProfileType() {
        return "CustomProfileClass";
    }
    
    @Override
    protected String getAttributeName() {
        return "whatever";
    }
    
    @Override
    public void testBuildProfileOK() {
    	try {
	        final Map<String, Object> attributes = new HashMap<String, Object>();
	        attributes.put(getAttributeName(), VALUE);
	        final UserProfile userProfile = ProfileHelper.buildUserProfileByClassCompleteName(getProfileType() + "#" + STRING_ID, attributes, getProfileClass().getName());
	        assertNotNull(userProfile);
	        assertEquals(STRING_ID, userProfile.getId());
	        assertEquals(getProfileType() + "#" + STRING_ID, userProfile.getTypedId());
	        final Map<String, Object> attributesProfile = userProfile.getAttributes();
	        assertEquals(1, attributesProfile.size());
	        assertEquals(VALUE, attributesProfile.get(getAttributeName()));
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    
    @Override
    public void testBuildProfileNoAttribute() {
    	try {
    		assertNotNull(ProfileHelper.buildUserProfileByClassCompleteName(getProfileType() + "#" + STRING_ID, EMPTY_MAP, getProfileClass().getName()));
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
}
