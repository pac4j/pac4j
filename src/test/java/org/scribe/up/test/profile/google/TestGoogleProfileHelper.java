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
package org.scribe.up.test.profile.google;

import java.util.List;

import junit.framework.TestCase;

import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.google.GoogleObject;
import org.scribe.up.profile.google.GoogleProfileHelper;

/**
 * This class tests the GoogleProfileHelper class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestGoogleProfileHelper extends TestCase {
    
    private static final String VALUE = "value";
    
    private static final String TYPE = "type";
    
    private static final String GOOD_JSON = "[ {\"value\" : \"" + VALUE + "\", \"type\" : \"" + TYPE + "\"} ]";
    
    private static final String BAD_JSON = "[ {\"key\" : \"" + VALUE + "\", \"key2\" : \"" + TYPE + "\"} ]";
    
    public void testGetListNull() {
        List<GoogleObject> list = GoogleProfileHelper.getListGoogleObject(null);
        assertNotNull(list);
        assertEquals(0, list.size());
    }
    
    public void testGetListBadKeys() {
        List<GoogleObject> list = GoogleProfileHelper.getListGoogleObject(JsonHelper.getFirstNode(BAD_JSON));
        assertNotNull(list);
        assertEquals(1, list.size());
        GoogleObject googleObject = list.get(0);
        assertNull(googleObject.getValue());
        assertNull(googleObject.getType());
    }
    
    public void testGetListOk() {
        List<GoogleObject> list = GoogleProfileHelper.getListGoogleObject(JsonHelper.getFirstNode(GOOD_JSON));
        assertNotNull(list);
        assertEquals(1, list.size());
        GoogleObject googleObject = list.get(0);
        assertEquals(VALUE, googleObject.getValue());
        assertEquals(TYPE, googleObject.getType());
    }
    
}
