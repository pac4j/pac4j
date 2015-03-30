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
 * This class tests the {@link FacebookGroup} class.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class TestFacebookGroup extends TestCase implements TestsConstants {
    
    private static final int VERSION = 1;
    
    private static final boolean ADMIN = true;
    
    private static final int ORDER = 1;
    
    private static final String GOOD_JSON = "{ \"version\": " + VERSION + ", \"name\": \"" + NAME + "\", \"id\": \""
                                            + STRING_ID + "\", \"administrator\": " + ADMIN + ", \"bookmark_order\": " + ORDER
                                            + "}";
    
    public void testNull() {
        final FacebookGroup facebookGroup = new FacebookGroup();
        facebookGroup.buildFrom(null);
        assertNull(facebookGroup.getVersion());
        assertNull(facebookGroup.getName());
        assertNull(facebookGroup.getId());
        assertNull(facebookGroup.getAdministrator());
        assertNull(facebookGroup.getBookmarkOrder());
    }
    
    public void testBadJson() {
        final FacebookGroup facebookGroup = new FacebookGroup();
        facebookGroup.buildFrom(BAD_JSON);
        assertNull(facebookGroup.getVersion());
        assertNull(facebookGroup.getName());
        assertNull(facebookGroup.getId());
        assertNull(facebookGroup.getAdministrator());
        assertNull(facebookGroup.getBookmarkOrder());
    }
    
    public void testGoodJson() {
        final FacebookGroup facebookGroup = new FacebookGroup();
        facebookGroup.buildFrom(GOOD_JSON);
        assertEquals(VERSION, facebookGroup.getVersion().intValue());
        assertEquals(NAME, facebookGroup.getName());
        assertEquals(STRING_ID, facebookGroup.getId());
        assertTrue(facebookGroup.getAdministrator());
        assertEquals(ORDER, facebookGroup.getBookmarkOrder().intValue());
    }
}
