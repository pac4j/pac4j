/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.test.profile.facebook;

import junit.framework.TestCase;

import org.scribe.up.profile.facebook.FacebookGroup;

/**
 * This class tests the {@link org.scribe.up.profile.facebook.FacebookGroup} class.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class TestFacebookGroup extends TestCase {
    
    private static final int VERSION = 1;
    
    private static final String NAME = "name";
    
    private static final String ID = "id";
    
    private static final boolean ADMIN = true;
    
    private static final int ORDER = 1;
    
    private static final String GOOD_JSON = "{ \"version\": " + VERSION + ", \"name\": \"" + NAME + "\", \"id\": \""
                                            + ID + "\", \"administrator\": " + ADMIN + ", \"bookmark_order\": " + ORDER
                                            + "}";
    
    private static final String BAD_JSON = "{ }";
    
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
        assertEquals(ID, facebookGroup.getId());
        assertTrue(facebookGroup.getAdministrator());
        assertEquals(ORDER, facebookGroup.getBookmarkOrder().intValue());
    }
}
