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
 * This class tests the {@link FacebookApplication} class.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class TestFacebookApplication extends TestCase implements TestsConstants {
    
    private static final String GOOD_JSON = "{\"id\" : \"" + STRING_ID + "\", \"name\" : \"" + NAME + "\", \"namespace\" : \""
                                            + NAMESPACE + "\"}";
    
    public void testNull() {
        final FacebookApplication facebookApplication = new FacebookApplication();
        facebookApplication.buildFrom(null);
        assertNull(facebookApplication.getId());
        assertNull(facebookApplication.getName());
        assertNull(facebookApplication.getNamespace());
    }
    
    public void testBadJson() {
        final FacebookApplication facebookApplication = new FacebookApplication();
        facebookApplication.buildFrom(BAD_JSON);
        assertNull(facebookApplication.getId());
        assertNull(facebookApplication.getName());
        assertNull(facebookApplication.getNamespace());
    }
    
    public void testGoodJson() {
        final FacebookApplication facebookApplication = new FacebookApplication();
        facebookApplication.buildFrom(GOOD_JSON);
        assertEquals(STRING_ID, facebookApplication.getId());
        assertEquals(NAME, facebookApplication.getName());
        assertEquals(NAMESPACE, facebookApplication.getNamespace());
    }
}
