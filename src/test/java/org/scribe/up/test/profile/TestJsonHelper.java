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
package org.scribe.up.test.profile;

import junit.framework.TestCase;

import org.scribe.up.profile.JsonHelper;

/**
 * This class tests the JsonHelper class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class TestJsonHelper extends TestCase {
    
    private static final String KEY = "key";
    
    private static final String VALUE = "value";
    
    private static final String GOOD_JSON = "{ \"" + KEY + "\" : \"" + VALUE + "\" }";
    
    private static final String BAD_JSON = "this_is_definitively_not_a_json_text";
    
    public void testGetFirstNodeOk() {
        assertNotNull(JsonHelper.getFirstNode(GOOD_JSON));
    }
    
    public void testGetFirstNodeKo() {
        assertNull(JsonHelper.getFirstNode(BAD_JSON));
    }
    
    public void testGetTextValueNull() {
        assertNull(JsonHelper.getTextValue(null, KEY));
    }
    
    public void testGetTextValueBadKey() {
        assertNull(JsonHelper.getTextValue(JsonHelper.getFirstNode(GOOD_JSON), "bad" + KEY));
    }
    
    public void testGetTextValueOk() {
        assertEquals(VALUE, JsonHelper.getTextValue(JsonHelper.getFirstNode(GOOD_JSON), KEY));
    }
}
