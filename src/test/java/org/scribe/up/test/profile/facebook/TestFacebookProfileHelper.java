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

import java.util.List;

import junit.framework.TestCase;

import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.facebook.FacebookObject;
import org.scribe.up.profile.facebook.FacebookProfileHelper;

/**
 * This class tests the FacebookProfileHelper class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestFacebookProfileHelper extends TestCase {
    
    private static final String ID = "12123112";
    
    private static final String VALUE = "value";
    
    private static final String GOOD_JSON = "[ {\"id\" : \"" + ID + "\", \"name\" : \"" + VALUE + "\"} ]";
    
    private static final String BAD_JSON = "[ {\"key\" : \"" + ID + "\", \"key2\" : \"" + VALUE + "\"} ]";
    
    public void testGetListNull() {
        List<FacebookObject> list = FacebookProfileHelper.getListFacebookObject(null);
        assertNotNull(list);
        assertEquals(0, list.size());
    }
    
    public void testGetListBadKeys() {
        List<FacebookObject> list = FacebookProfileHelper.getListFacebookObject(JsonHelper.getFirstNode(BAD_JSON));
        assertNotNull(list);
        assertEquals(1, list.size());
        FacebookObject facebookObject = list.get(0);
        assertNull(facebookObject.getId());
        assertNull(facebookObject.getName());
    }
    
    public void testGetListOk() {
        List<FacebookObject> list = FacebookProfileHelper.getListFacebookObject(JsonHelper.getFirstNode(GOOD_JSON));
        assertNotNull(list);
        assertEquals(1, list.size());
        FacebookObject facebookObject = list.get(0);
        assertEquals(ID, facebookObject.getId());
        assertEquals(VALUE, facebookObject.getName());
    }
}
