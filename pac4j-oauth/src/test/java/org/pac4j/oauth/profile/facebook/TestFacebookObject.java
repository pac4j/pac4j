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
package org.pac4j.oauth.profile.facebook;

import junit.framework.TestCase;

import org.pac4j.core.util.TestsConstants;

/**
 * This class tests the {@link FacebookObject} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestFacebookObject extends TestCase implements TestsConstants {
    
    private static final String GOOD_JSON = "{\"id\" : \"" + STRING_ID + "\", \"name\" : \"" + NAME + "\"}";
    
    public void testNull() {
        final FacebookObject facebookObject = new FacebookObject();
        facebookObject.buildFrom(null);
        assertNull(facebookObject.getId());
        assertNull(facebookObject.getName());
    }
    
    public void testBadJson() {
        final FacebookObject facebookObject = new FacebookObject();
        facebookObject.buildFrom(BAD_JSON);
        assertNull(facebookObject.getId());
        assertNull(facebookObject.getName());
    }
    
    public void testGoodJson() {
        final FacebookObject facebookObject = new FacebookObject();
        facebookObject.buildFrom(GOOD_JSON);
        assertEquals(STRING_ID, facebookObject.getId());
        assertEquals(NAME, facebookObject.getName());
    }
}
