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
package org.pac4j.oauth.profile;

import junit.framework.TestCase;

import org.pac4j.core.util.TestsConstants;

/**
 * This class tests the {@link JsonHelper} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestJsonHelper extends TestCase implements TestsConstants {
    
    private static final String GOOD_TEXT_JSON = "{ \"" + KEY + "\" : \"" + VALUE + "\" }";
    
    private static final String GOOD_BOOLEAN_JSON = "{ \"" + KEY + "\" : " + Boolean.TRUE + " }";
    
    private static final String GOOD_NUMBER_JSON = "{ \"" + KEY + "\" : 1 }";
    
    private static final String GOOD_NODE_JSON = "{ \"" + KEY + "\" : " + GOOD_TEXT_JSON + " }";
    
    private static final String BAD_JSON = "this_is_definitively_not_a_json_text";
    
    public void testGetFirstNodeOk() {
        assertNotNull(JsonHelper.getFirstNode(GOOD_TEXT_JSON));
    }
    
    public void testGetFirstNodeKo() {
        assertNull(JsonHelper.getFirstNode(BAD_JSON));
    }
    
    public void testGetText() {
        assertEquals(VALUE, JsonHelper.get(JsonHelper.getFirstNode(GOOD_TEXT_JSON), KEY));
    }
    
    public void testGetNull() {
        assertNull(JsonHelper.get(null, KEY));
    }
    
    public void testGetBadKey() {
        assertNull(JsonHelper.get(JsonHelper.getFirstNode(GOOD_TEXT_JSON), "bad" + KEY));
    }
    
    public void testGetBoolean() {
        assertEquals(Boolean.TRUE, JsonHelper.get(JsonHelper.getFirstNode(GOOD_BOOLEAN_JSON), KEY));
    }
    
    public void testGetNumber() {
        assertEquals(1, JsonHelper.get(JsonHelper.getFirstNode(GOOD_NUMBER_JSON), KEY));
    }
    
    public void testGetNode() {
        assertEquals(JsonHelper.getFirstNode(GOOD_TEXT_JSON),
                     JsonHelper.get(JsonHelper.getFirstNode(GOOD_NODE_JSON), KEY));
    }
}
